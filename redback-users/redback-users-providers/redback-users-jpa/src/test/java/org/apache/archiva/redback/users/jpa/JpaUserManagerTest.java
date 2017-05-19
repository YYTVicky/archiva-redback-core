package org.apache.archiva.redback.users.jpa;

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

import org.apache.archiva.redback.policy.UserSecurityPolicy;
import org.apache.archiva.redback.users.User;
import org.apache.archiva.redback.users.UserManager;
import org.apache.archiva.redback.users.UserManagerException;
import org.apache.archiva.redback.users.UserNotFoundException;
import org.apache.archiva.redback.users.jpa.model.JpaUser;
import org.apache.archiva.redback.users.provider.test.AbstractUserManagerTestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * Test for the JPA User Manager
 *
 * @author <a href="mailto:martin_s@apache.org">Martin Stockhammer</a>
 */
@Transactional
public class JpaUserManagerTest extends AbstractUserManagerTestCase {

    Log log = LogFactory.getLog(JpaUserManagerTest.class);

    @Inject
    @Named("userManager#jpa")
    UserManager jpaUserManager;

    @Inject
    EntityManagerFactory entityManagerFactory;


    @Inject
    private UserSecurityPolicy securityPolicy;

    @Before
    @Override
    public void setUp() throws Exception {

        super.setUp();
        assertNotNull(jpaUserManager);
        super.setUserManager(jpaUserManager);
        log.info("injected usermanager "+jpaUserManager);

    }

    @Test
    public void testInit() {
        assertNotNull(jpaUserManager);
        jpaUserManager.initialize();
    }





}
