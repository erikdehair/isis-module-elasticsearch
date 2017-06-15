package org.isisaddons.module.elasticsearch.search.elastic;

import org.apache.isis.applib.annotation.Programmatic;

public interface Indexable {
    @Programmatic
    String getIndexId();

    @Programmatic
    String getTenancy();

    @Programmatic
    String getSearchResultSummary();

    @Programmatic
    default boolean isIndexable() {
        return true;
    }
}
