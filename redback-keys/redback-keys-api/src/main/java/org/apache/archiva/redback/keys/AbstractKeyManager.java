package org.apache.archiva.redback.keys;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;

/**
 * AbstractKeyManager 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 *
 */
public abstract class AbstractKeyManager
    implements KeyManager
{
    protected Logger log = LoggerFactory.getLogger( getClass() );
    
    private static final int KEY_LENGTH = 16;

    private static final boolean SECURE = true;

    private boolean randomMode = SECURE;

    private SecureRandom secureRandom;

    private Random random;

    /**
     * Generate a UUID using <a href="http://www.ietf.org/rfc/rfc4122.txt">RFC 4122</a> UUID generation of a 
     * type 4 or randomly generated UUID.
     * 
     * @return the 32 character long UUID string.
     * @throws KeyManagerException
     */
    protected String generateUUID()
        throws KeyManagerException
    {
        return UUID.randomUUID().toString();
    }

    /**
     * Tests the key to see if it is expired or not.
     * 
     * If the key is expired, a call to {@link #deleteKey(AuthenticationKey)} is issued,
     * and a {@link KeyNotFoundException} is thrown.
     * 
     * @param authkey the key to test.
     * @throws KeyNotFoundException if the key is expired.
     * @throws KeyManagerException if there was a problem removing the key.
     */
    protected void assertNotExpired( AuthenticationKey authkey )
        throws KeyNotFoundException, KeyManagerException
    {
        if ( authkey.getDateExpires() == null )
        {
            // No expiration means a permanent entry.
            return;
        }
    
        // Test for expiration.
        Calendar now = getNowGMT();
        Calendar expiration = getNowGMT();
        expiration.setTime( authkey.getDateExpires() );
    
        if ( now.after( expiration ) )
        {
            deleteKey( authkey );
            throw new KeyNotFoundException( "Key [" + authkey.getKey() + "] has expired." );
        }
    }

    protected Calendar getNowGMT()
    {
        return Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) );
    }

    public void setRandomMode( boolean randomMode )
    {
        this.randomMode = randomMode;
    }

    public boolean isRandomMode()
    {
        return randomMode;
    }

    public void removeExpiredKeys()
        throws KeyManagerException
    {
        List<AuthenticationKey> allKeys = getAllKeys();

        Calendar now = getNowGMT();
        Calendar expiration = getNowGMT();

        log.info( "Removing expired keys." );
        for ( AuthenticationKey authkey : allKeys )
        {
            if ( authkey.getDateExpires() != null )
            {
                expiration.setTime( authkey.getDateExpires() );

                if ( now.after( expiration ) )
                {
                    deleteKey( authkey );
                }
            }
        }
        log.info( "Expired keys removed." );
    }
}
