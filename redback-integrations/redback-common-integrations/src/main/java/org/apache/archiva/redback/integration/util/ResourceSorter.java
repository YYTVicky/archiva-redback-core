package org.apache.archiva.redback.integration.util;

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

import org.apache.archiva.redback.rbac.Resource;

import java.util.Comparator;

/**
 * ResourceSorter
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 *
 */
public class ResourceSorter
    implements Comparator<Resource>
{

    public int compare( Resource r1, Resource r2 )
    {
        if ( ( r1 == null ) && ( r2 == null ) )
        {
            return 0;
        }

        if ( ( r1 == null ) && ( r2 != null ) )
        {
            return -1;
        }

        if ( ( r1 != null ) && ( r2 == null ) )
        {
            return 1;
        }

        return r1.getIdentifier().compareToIgnoreCase( r2.getIdentifier() );
    }

}
