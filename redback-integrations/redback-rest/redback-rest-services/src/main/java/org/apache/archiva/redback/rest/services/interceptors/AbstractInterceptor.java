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

import org.apache.archiva.redback.authorization.RedbackAuthorization;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import java.lang.reflect.Method;

/**
 * @author Olivier Lamy
 * @since 1.3
 */
public abstract class AbstractInterceptor
{

    private final Logger log = LoggerFactory.getLogger( getClass() );

    @Context
    private HttpServletRequest httpServletRequest;

    @Context
    private HttpServletResponse httpServletResponse;

    public HttpServletRequest getHttpServletRequest( )
    {
        return httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse( )
    {
        return httpServletResponse;
    }

    public RedbackAuthorization getRedbackAuthorization( Message message )
    {
        OperationResourceInfo operationResourceInfo = message.getExchange().get( OperationResourceInfo.class );
        if ( operationResourceInfo == null )
        {
            return null;
        }

        Method method = operationResourceInfo.getAnnotatedMethod();

        RedbackAuthorization redbackAuthorization = method.getAnnotation( RedbackAuthorization.class );

        log.debug( "class {}, resourceClass {}, method {}, redbackAuthorization {}", //
                   operationResourceInfo.getClassResourceInfo().getServiceClass(), //
                   operationResourceInfo.getClassResourceInfo().getResourceClass(), //
                   method, //
                   redbackAuthorization );

        return redbackAuthorization;
    }

    public RedbackAuthorization getRedbackAuthorization( ResourceInfo resourceInfo ) {
        Method method = resourceInfo.getResourceMethod( );
        RedbackAuthorization redbackAuthorization = AnnotationUtils.findAnnotation( method, RedbackAuthorization.class );
        log.debug( "resourceClass {}, method {}, redbackAuthorization {}", //
                resourceInfo.getResourceClass( ), //
                method, //
                redbackAuthorization );
        return redbackAuthorization;
    }
}
