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

import com.google.common.collect.ComparisonChain;
import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.isisaddons.module.elasticsearch.search.elastic.indexing.Indexable;

import javax.inject.Inject;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema="elasticsearch")
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy=VersionStrategy.VERSION_NUMBER, 
        column="version")
@DomainObject(
        objectType = "ELASTICSEARCH_DEMO_OBJECT"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class ElasticSearchDemoObject implements Comparable<ElasticSearchDemoObject>, Indexable {

    //region > name (property)
    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence="1")
    @Getter @Setter
    private String name;
    //endregion

    @javax.jdo.annotations.Column(allowsNull="true")
    @Getter @Setter
    private String description;

    @Override
    public String getIndexId() {
        return bookmarkService2.bookmarkFor(this).getIdentifier();
    }

    @Override
    public String getTenancy() {
        return "foo";
    }

    @Override
    public String getSearchResultSummary() {
        return "This is the summary of "+ getName() + " with description "+ getDescription();
    }

    @Override
    public boolean isIndexable() {
        return true;
    }

    //region > compareTo

    @Override
    public int compareTo(ElasticSearchDemoObject other) {
        return ComparisonChain.start()
                .compare(getName(), other.getName())
                .result();
    }

    //endregion

    @Override
    public String toString(){
        return getClass().getName() +"[name="+getName()+"]";
    }

    //region > injected services

    @javax.inject.Inject
    @SuppressWarnings("unused")
    private DomainObjectContainer container;

    @Inject
    private BookmarkService2 bookmarkService2;
    //endregion

}
