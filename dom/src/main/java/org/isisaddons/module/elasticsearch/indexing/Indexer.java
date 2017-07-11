package org.isisaddons.module.elasticsearch.indexing;


public abstract class Indexer {
    protected Indexable indexable;

    public abstract AbstractIndex createUpdatedIndex();

    public Indexer(Indexable indexable) {
        this.indexable = indexable;
    }
}
