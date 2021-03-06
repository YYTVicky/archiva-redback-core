package org.apache.archiva.redback.integration;

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

import junit.framework.TestCase;

import java.util.Properties;

/**
 * HttpUtilsTest
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 *
 */
public class HttpUtilsTest
    extends TestCase
{
    public void testComplexHeaderToProperties()
    {
        String rawheader = "realm=\"Somewhere Over The Rainbow\", domain = \"kansas.co.us\", nonce=\"65743ABCF";

        Properties props = HttpUtils.complexHeaderToProperties( rawheader, ",", "=" );

        assertNotNull( props );
        assertEquals( 3, props.size() );
        assertEquals( "Somewhere Over The Rainbow", props.get( "realm" ) );
        assertEquals( "kansas.co.us", props.get( "domain" ) );
        assertEquals( "65743ABCF", props.get( "nonce" ) );
    }
}
