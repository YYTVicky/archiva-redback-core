package org.apache.archiva.redback.rest.services.interceptors;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.archiva.redback.authentication.AuthenticationResult;
import org.apache.archiva.redback.authorization.AuthorizationException;
import org.apache.archiva.redback.authorization.AuthorizationResult;
import org.apache.archiva.redback.authorization.RedbackAuthorization;
import org.apache.archiva.redback.integration.filter.authentication.basic.HttpBasicAuthentication;
import org.apache.archiva.redback.system.SecuritySession;
import org.apache.archiva.redback.system.SecuritySystem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 * @author Olivier Lamy
 * @since 1.3
 */
@Service( "permissionInterceptor#rest" )
@Provider
public class PermissionsInterceptor
    extends AbstractInterceptor
    implements ContainerRequestFilter
{

    @Inject
    @Named( value = "securitySystem" )
    private SecuritySystem securitySystem;

    @Inject
    @Named( value = "httpAuthenticator#basic" )
    private HttpBasicAuthentication httpAuthenticator;

    @Context
    private ResourceInfo resourceInfo;

    private final Logger log = LoggerFactory.getLogger( getClass() );

    public void filter( ContainerRequestContext containerRequestContext )
    {

        RedbackAuthorization redbackAuthorization = getRedbackAuthorization( resourceInfo );

        if ( redbackAuthorization != null )
        {
            if ( redbackAuthorization.noRestriction() )
            {
                log.debug( "redbackAuthorization.noRestriction() so skip permission check" );
                // we are fine this services is marked as non restrictive access
                return;
            }
            String[] permissions = redbackAuthorization.permissions();
            //olamy: no value is an array with an empty String
            if ( permissions != null && permissions.length > 0 //
                && !( permissions.length == 1 && StringUtils.isEmpty( permissions[0] ) ) )
            {
                HttpServletRequest request = getHttpServletRequest( );
                SecuritySession securitySession = httpAuthenticator.getSecuritySession( request.getSession() );
                AuthenticationResult authenticationResult = getAuthenticationResult( containerRequestContext, httpAuthenticator, request );

                log.debug( "authenticationResult from message: {}", authenticationResult );

                if ( authenticationResult != null && authenticationResult.isAuthenticated() )
                {

                    for ( String permission : permissions )
                    {
                        log.debug( "check permission: {} with securitySession {}", permission, securitySession );
                        if ( StringUtils.isBlank( permission ) )
                        {
                            continue;
                        }
                        try
                        {
                            String resource = redbackAuthorization.resource();
                            if (resource.startsWith("{") && resource.endsWith("}") && resource.length()>2) {
                                resource = getMethodParameter(containerRequestContext, resource.substring(1,resource.length()-1));
                                log.debug("Found resource from annotated parameter: {}",resource);
                            }

                            AuthorizationResult authorizationResult =
                                securitySystem.authorize( authenticationResult.getUser(), permission, //
                                                          StringUtils.isBlank( resource ) //
                                                              ? null : resource );
                             if ( authenticationResult != null && authorizationResult.isAuthorized() )
                            {
                                log.debug( "isAuthorized for permission {}", permission );
                                return;
                            }
                            else
                            {
                                if ( securitySession != null && securitySession.getUser() != null )
                                {
                                    log.debug( "user {} not authorized for permission {}", //
                                               securitySession.getUser().getUsername(), //
                                               permission );
                                }
                            }
                        }
                        catch ( AuthorizationException e )
                        {
                            log.debug( " AuthorizationException " + e.getMessage() //
                                           + " checking permission " + permission, e );
                            containerRequestContext.abortWith( Response.status( Response.Status.FORBIDDEN ).build() );
                            return;
                        }
                    }
                }
                else
                {
                    if ( securitySession != null && securitySession.getUser() != null )
                    {
                        log.debug( "user {} not authenticated", securitySession.getUser().getUsername() );
                    }
                    return;
                }
            }
            else
            {
                if ( redbackAuthorization.noPermission() )
                {
                    log.debug( "path {} doesn't need special permission", containerRequestContext.getUriInfo().getRequestUri() );
                    return;
                }
                containerRequestContext.abortWith( Response.status( Response.Status.FORBIDDEN ).build() );
                return;
            }
        }

        log.warn( "http path {} doesn't contain any informations regarding permissions ", //
                  containerRequestContext.getUriInfo().getRequestUri() );
        // here we failed to authenticate so 403 as there is no detail on karma for this
        // it must be marked as it's exposed
        containerRequestContext.abortWith( Response.status( Response.Status.FORBIDDEN ).build() );

    }

    /*
     * Extracts a request parameter value from the message. Currently checks only path and query parameter.
     */
    private String getMethodParameter( final ContainerRequestContext requestContext, final String parameterName ) {
        UriInfo uriInfo = requestContext.getUriInfo( );
        if (uriInfo.getPathParameters().containsKey( parameterName )) {
            return uriInfo.getPathParameters( ).get( parameterName ).get( 0 );
        } else if (uriInfo.getQueryParameters().containsKey( parameterName )) {
            return uriInfo.getQueryParameters( ).get( parameterName ).get( 0 );
        }
        return "";
    }


}
