package org.isisaddons.module.elasticsearch.search.elastic.indexing;


public abstract class Indexer {
    protected Indexable indexable;

    public abstract AbstractIndex createUpdatedIndex();

    public Indexer(Indexable indexable) {
        this.indexable = indexable;
    }
}
