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
package org.isisaddons.module.elasticsearch.fixture.scripts;

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;
import org.isisaddons.module.elasticsearch.fixture.scripts.scenarios.AnotherElasticSearchDemoObjectsFixture;
import org.isisaddons.module.elasticsearch.fixture.scripts.scenarios.ElasticSearchDemoObjectsFixture;

import java.util.List;

/**
 * Enables fixtures to be installed from the application.
 */
@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        named = "Prototyping",
        menuOrder = "20",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY
)
public class ElasticSearchDemoObjectsFixturesService extends FixtureScripts {

    public ElasticSearchDemoObjectsFixturesService() {
        super(ElasticSearchDemoObjectsFixturesService.class.getPackage().getName());
    }

    @Override // compatibility with core 1.5.0
    public FixtureScript default0RunFixtureScript() {
        return findFixtureScriptFor(SimpleFixtureScript.class);
    }

    /**
     * Raising visibility to <tt>public</tt> so that choices are available for first param
     * of {@link #runFixtureScript(FixtureScript, String)}.
     */
    @Override
    public List<FixtureScript> choices0RunFixtureScript() {
        return super.choices0RunFixtureScript();
    }


    // //////////////////////////////////////

    @Action(
            restrictTo = RestrictTo.PROTOTYPING
    )
    @MemberOrder(sequence="20")
    public Object installFixturesAndReturnFirst() {
        final FixtureScript script = findFixtureScriptFor(ElasticSearchDemoObjectsFixture.class);
        final List<FixtureResult> run = script.run(null);
        return run.get(0).getObject();
    }

    @Action(
            restrictTo = RestrictTo.PROTOTYPING
    )
    @MemberOrder(sequence="20")
    public Object installOtherFixturesAndReturnFirst() {
        final FixtureScript script = findFixtureScriptFor(AnotherElasticSearchDemoObjectsFixture.class);
        final List<FixtureResult> run = script.run(null);
        return run.get(0).getObject();
    }


}
