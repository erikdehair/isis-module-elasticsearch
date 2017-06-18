package org.isisaddons.module.elasticsearch.search.elastic.indexing;


import org.apache.isis.applib.AbstractDomainObject;

public abstract class Indexer {
    protected Indexable indexable;

    public abstract AbstractIndex createUpdatedIndex();

    public Indexer(Indexable indexable) {
        this.indexable = indexable;
    }

    public static Indexer createIndexer(Indexable indexable) throws Exception {
        if (AbstractDomainObject.class.isAssignableFrom(indexable.getClass())) {
            //return new SubscriptionIndexer(indexable);
        } else {
            throw new Exception("Non-indexable class found: " + indexable.getClass().getName());
        }
        return null;
    }
}
