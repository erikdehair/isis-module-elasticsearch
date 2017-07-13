/*
 *  Copyright 2014 Dan Haywood
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
package org.isisaddons.module.elasticsearch.fixture.dom;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;

import java.util.List;

@DomainService(
        nature = NatureOfService.VIEW,
        repositoryFor = ElasticSearchDemoObject.class
)
@DomainServiceLayout(
        menuOrder = "10"
)
public class ElasticSearchDemoObjects {


    //region > listAll (action)

    @Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            bookmarking = BookmarkPolicy.AS_ROOT
    )
    @MemberOrder(sequence = "1")
    public List<ElasticSearchDemoObject> listAllDemoObjects() {
        return container.allInstances(ElasticSearchDemoObject.class);
    }

    @Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            bookmarking = BookmarkPolicy.AS_ROOT
    )
    @MemberOrder(sequence = "1")
    public List<AnotherElasticSearchDemoObject> listAllOtherDemoObjects() {
        return container.allInstances(AnotherElasticSearchDemoObject.class);
    }

    //endregion

    //region > create (action)
    
    @MemberOrder(sequence = "2")
    public ElasticSearchDemoObject createDemoObject(
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Description") String description) {
        final ElasticSearchDemoObject obj = container.newTransientInstance(ElasticSearchDemoObject.class);
        obj.setName(name);
        obj.setDescription(description);
        container.persistIfNotAlready(obj);
        return obj;
    }

    @MemberOrder(sequence = "2")
    public AnotherElasticSearchDemoObject createAnotherDemoObject(
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Description") String remarks) {
        final AnotherElasticSearchDemoObject obj = container.newTransientInstance(AnotherElasticSearchDemoObject.class);
        obj.setName(name);
        obj.setRemarks(remarks);
        container.persistIfNotAlready(obj);
        return obj;
    }

    //endregion

    //region > injected services

    @javax.inject.Inject 
    DomainObjectContainer container;

    //endregion

}
