/*
 * Copyright 2010-2013 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.tenant.dao;

import java.util.List;
import java.util.UUID;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.killbill.billing.tenant.TenantTestSuiteWithEmbeddedDb;
import org.killbill.billing.tenant.api.DefaultTenant;
import org.killbill.billing.util.security.shiro.KillbillCredentialsMatcher;

public class TestDefaultTenantDao extends TenantTestSuiteWithEmbeddedDb {

    @Test(groups = "slow")
    public void testWeCanStoreAndMatchCredentials() throws Exception {
        final DefaultTenant tenant = new DefaultTenant(UUID.randomUUID(), null, null, UUID.randomUUID().toString(),
                                                       UUID.randomUUID().toString(), UUID.randomUUID().toString());
        tenantDao.create(new TenantModelDao(tenant), internalCallContext);

        // Verify we can retrieve it
        Assert.assertEquals(tenantDao.getTenantByApiKey(tenant.getApiKey()).getId(), tenant.getId());

        // Verify we can authenticate against it
        final AuthenticationInfo authenticationInfo = tenantDao.getAuthenticationInfoForTenant(tenant.getId());

        // Good combo
        final AuthenticationToken goodToken = new UsernamePasswordToken(tenant.getApiKey(), tenant.getApiSecret());
        Assert.assertTrue(KillbillCredentialsMatcher.getCredentialsMatcher().doCredentialsMatch(goodToken, authenticationInfo));

        // Bad combo
        final AuthenticationToken badToken = new UsernamePasswordToken(tenant.getApiKey(), tenant.getApiSecret() + "T");
        Assert.assertFalse(KillbillCredentialsMatcher.getCredentialsMatcher().doCredentialsMatch(badToken, authenticationInfo));
    }

    @Test(groups = "slow")
    public void testTenantKeyValue() throws Exception {
        final DefaultTenant tenant = new DefaultTenant(UUID.randomUUID(), null, null, UUID.randomUUID().toString(),
                                                       UUID.randomUUID().toString(), UUID.randomUUID().toString());
        tenantDao.create(new TenantModelDao(tenant), internalCallContext);

        tenantDao .addTenantKeyValue("THE_KEY", "TheValue", false, internalCallContext);

        List<String> value = tenantDao.getTenantValueForKey("THE_KEY", internalCallContext);
        Assert.assertEquals(value.size(), 1);
        Assert.assertEquals(value.get(0), "TheValue");

        tenantDao.addTenantKeyValue("THE_KEY", "TheSecondValue", false, internalCallContext);
        value = tenantDao.getTenantValueForKey("THE_KEY", internalCallContext);
        Assert.assertEquals(value.size(), 2);

        value = tenantDao.getTenantValueForKey("THE_KEY", internalCallContext);
        Assert.assertEquals(value.size(), 2);

        tenantDao.deleteTenantKey("THE_KEY", internalCallContext);
        value = tenantDao.getTenantValueForKey("THE_KEY", internalCallContext);
        Assert.assertEquals(value.size(), 0);
    }
}
