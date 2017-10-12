package org.isisaddons.module.elasticsearch.indexing;

import org.apache.isis.applib.annotation.Programmatic;

public interface Indexable {
    /**
     * The object's internal Elastic Search id.
     * @return
     */
    @Programmatic
    default String getIndexId(){
        return null;
    }

    @Programmatic
    String getTenancy();

    /**
     * This value will be used as a summary on the result page.
     * @return
     */
    @Programmatic
    String getSearchResultSummary();

    /**
     * For some reason an indexable object shouldn't be indexed though.
     * @return
     */
    @Programmatic
    default boolean isIndexable() {
        return true;
    }
}
