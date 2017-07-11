package org.isisaddons.module.elasticsearch.fixture.dom;

import org.isisaddons.module.elasticsearch.indexing.AbstractIndex;
import org.isisaddons.module.elasticsearch.indexing.Indexable;
import org.isisaddons.module.elasticsearch.indexing.Indexer;

/**
 * Created by E. de Hair <e.dehair@pocos.nl> on 6/18/17.
 */
public class ElasticSearchDemoObjectIndexer extends Indexer {

    public ElasticSearchDemoObjectIndexer(Indexable indexable) {
        super(indexable);
    }

    @Override
    public AbstractIndex createUpdatedIndex() {
        ElasticSearchDemoObject p = (ElasticSearchDemoObject) indexable;
        ElasticSearchDemoObjectIndex index = new ElasticSearchDemoObjectIndex();
        index.setTenancy(p.getTenancy());
        index.setName(p.getName());
        index.setDescription(p.getDescription());
        return index;
    }
}
