package org.isisaddons.module.elasticsearch.indexing.spi;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.isisaddons.module.elasticsearch.indexing.Indexable;
import org.isisaddons.module.elasticsearch.indexing.Indexer;

/**
 * Created by erik on 6/22/17.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public interface IndexerFactory {
    Indexer createIndexer(Indexable indexable) throws Exception;
}
