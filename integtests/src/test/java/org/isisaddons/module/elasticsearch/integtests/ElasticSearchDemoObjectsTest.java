/*
 *  Copyright 2014~2015 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.elasticsearch.integtests;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.assertj.core.api.Assertions;
import org.isisaddons.module.elasticsearch.fixture.dom.AnotherElasticSearchDemoObject;
import org.isisaddons.module.elasticsearch.fixture.dom.ElasticSearchDemoObject;
import org.isisaddons.module.elasticsearch.fixture.dom.ElasticSearchDemoObjects;
import org.isisaddons.module.elasticsearch.fixture.scripts.scenarios.AnotherElasticSearchDemoObjectsFixture;
import org.isisaddons.module.elasticsearch.fixture.scripts.scenarios.ElasticSearchDemoObjectsFixture;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;


public class ElasticSearchDemoObjectsTest extends ElasticSearchModuleIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    private ElasticSearchDemoObjects elasticSearchDemoObjects;

    @Before
    public void setUpData() throws Exception {
        fixtureScripts.runFixtureScript(new ElasticSearchDemoObjectsFixture(), null);
        fixtureScripts.runFixtureScript(new AnotherElasticSearchDemoObjectsFixture(), null);
    }


    @Test
    public void listAll() throws Exception {

        final List<ElasticSearchDemoObject> all = wrap(elasticSearchDemoObjects).listAllDemoObjects();
        Assertions.assertThat(all.size()).isEqualTo(3);
        
        ElasticSearchDemoObject elasticSearchDemoObject = wrap(all.get(0));
        Assertions.assertThat(elasticSearchDemoObject.getName()).isEqualTo("Foo");
    }
    
    @Test
    public void create() throws Exception {

        wrap(elasticSearchDemoObjects).createDemoObject("Faz","FazDescription");
        
        final List<ElasticSearchDemoObject> all = wrap(elasticSearchDemoObjects).listAllDemoObjects();
        Assertions.assertThat(all.size()).isEqualTo(4);
    }

    @Test
    public void listAllOther() throws Exception {

        final List<AnotherElasticSearchDemoObject> all = wrap(elasticSearchDemoObjects).listAllOtherDemoObjects();
        Assertions.assertThat(all.size()).isEqualTo(3);

        AnotherElasticSearchDemoObject elasticSearchDemoObject = wrap(all.get(0));
        Assertions.assertThat(elasticSearchDemoObject.getName()).isEqualTo("Foo");
    }

    @Test
    public void createOther() throws Exception {

        wrap(elasticSearchDemoObjects).createAnotherDemoObject("Faz","FazDescription");

        final List<AnotherElasticSearchDemoObject> all = wrap(elasticSearchDemoObjects).listAllOtherDemoObjects();
        Assertions.assertThat(all.size()).isEqualTo(4);
    }

}