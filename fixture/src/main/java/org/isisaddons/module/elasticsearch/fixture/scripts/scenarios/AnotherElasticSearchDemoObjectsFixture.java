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
package org.isisaddons.module.elasticsearch.fixture.scripts.scenarios;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.isisaddons.module.elasticsearch.fixture.dom.AnotherElasticSearchDemoObject;
import org.isisaddons.module.elasticsearch.fixture.dom.ElasticSearchDemoObjects;
import org.isisaddons.module.elasticsearch.fixture.scripts.teardown.AnotherElasticSearchDemoObjectsTearDownFixture;

public class AnotherElasticSearchDemoObjectsFixture extends DiscoverableFixtureScript {

    public AnotherElasticSearchDemoObjectsFixture() {
        withDiscoverability(Discoverability.DISCOVERABLE);
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
	executionContext.executeChild(this, new AnotherElasticSearchDemoObjectsTearDownFixture());

        // create
        create("Foo", "Lorem ipsum", executionContext);
        create("Bar", "Bar dolor sit amet,", executionContext);
        create("Baz", "Foo consectetur adipiscing elit", executionContext);
    }

    // //////////////////////////////////////

    private AnotherElasticSearchDemoObject create(final String name, final String description, final ExecutionContext executionContext) {
        return executionContext.addResult(this, elasticSearchDemoObjects.createAnotherDemoObject(name, description));
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private ElasticSearchDemoObjects elasticSearchDemoObjects;

}
