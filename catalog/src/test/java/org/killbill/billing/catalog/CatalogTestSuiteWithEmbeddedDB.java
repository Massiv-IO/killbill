/*
 * Copyright 2014-2015 Groupon, Inc
 * Copyright 2014-2015 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
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

package org.killbill.billing.catalog;

import org.killbill.billing.GuicyKillbillTestSuiteWithEmbeddedDB;
import org.killbill.billing.catalog.dao.CatalogOverrideDao;
import org.killbill.billing.catalog.glue.TestCatalogModuleWithEmbeddedDB;
import org.killbill.billing.platform.api.KillbillConfigSource;
import org.skife.jdbi.v2.IDBI;
import org.testng.annotations.BeforeClass;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class CatalogTestSuiteWithEmbeddedDB extends GuicyKillbillTestSuiteWithEmbeddedDB {

    @Inject
    protected CatalogOverrideDao catalogOverrideDao;

    @Inject
    protected IDBI dbi;

    @Override
    protected KillbillConfigSource getConfigSource() {
        return getConfigSource("/resource.properties");
    }

    @BeforeClass(groups = "slow")
    protected void beforeClass() throws Exception {
        final Injector injector = Guice.createInjector(new TestCatalogModuleWithEmbeddedDB(configSource));
        injector.injectMembers(this);
    }

}
