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
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.isisaddons.module.elasticsearch.indexing.Indexable;

import javax.inject.Inject;
import javax.jdo.annotations.*;

@PersistenceCapable(
        identityType=IdentityType.APPLICATION,
        schema="elasticsearch")
@Version(
        strategy=VersionStrategy.VERSION_NUMBER,
        column="version")
@DomainObject(
        objectType = "ANOTHER_ELASTICSEARCH_DEMO_OBJECT"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class AnotherElasticSearchDemoObject implements Comparable<AnotherElasticSearchDemoObject>, Indexable {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Getter @Setter
    private int id;

    //region > name (property)
    @Column(allowsNull="false")
    @Title(sequence="1")
    @Getter @Setter
    private String name;
    //endregion

    @Column(allowsNull="true")
    @Getter @Setter
    private String remarks;

    @Override
    public String getTenancy() {
        return "foo";
    }

    @Override
    public String getSearchResultSummary() {
        return "This is the summary of "+ toString() +" with remarks "+ getRemarks() +" and id "+ getId();
    }

    @Override
    public boolean isIndexable() {
        return true;
    }

    //region > compareTo

    @Override
    public int compareTo(AnotherElasticSearchDemoObject other) {
        return ComparisonChain.start()
                .compare(getId(), other.getId())
                .compare(getName(), other.getName())
                .result();
    }

    //endregion

    @Override
    public String toString(){
        return getClass().getName() +"[name="+getName()+"]";
    }
}
