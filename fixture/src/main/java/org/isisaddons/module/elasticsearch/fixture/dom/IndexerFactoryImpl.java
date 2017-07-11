package org.isisaddons.module.elasticsearch.fixture.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.isisaddons.module.elasticsearch.indexing.Indexable;
import org.isisaddons.module.elasticsearch.indexing.Indexer;
import org.isisaddons.module.elasticsearch.indexing.IndexerFactory;

/**
 * Created by E. de Hair <e.dehair@pocos.nl> on 6/22/17.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class IndexerFactoryImpl implements IndexerFactory {

    public Indexer createIndexer(Indexable indexable) throws Exception {
        if (ElasticSearchDemoObject.class.isAssignableFrom(indexable.getClass())) {
            return new ElasticSearchDemoObjectIndexer(indexable);
        } else {
            throw new Exception("Non-indexable class found: " + indexable.getClass().getName());
        }
    }
}
