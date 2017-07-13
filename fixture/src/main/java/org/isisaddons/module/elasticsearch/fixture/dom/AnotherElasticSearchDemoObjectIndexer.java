package org.isisaddons.module.elasticsearch.fixture.dom;

import org.isisaddons.module.elasticsearch.indexing.AbstractIndex;
import org.isisaddons.module.elasticsearch.indexing.Indexable;
import org.isisaddons.module.elasticsearch.indexing.Indexer;

/**
 * Created by E. de Hair <e.dehair@pocos.nl> on 6/18/17.
 */
public class AnotherElasticSearchDemoObjectIndexer extends Indexer {

    public AnotherElasticSearchDemoObjectIndexer(Indexable indexable) {
        super(indexable);
    }

    @Override
    public AbstractIndex createUpdatedIndex() {
        AnotherElasticSearchDemoObject p = (AnotherElasticSearchDemoObject) indexable;
        AnotherElasticSearchDemoObjectIndex index = new AnotherElasticSearchDemoObjectIndex();
        index.setTenancy(p.getTenancy());
        index.setName(p.getName());
        index.setRemarks(p.getRemarks());
        return index;
    }
}
