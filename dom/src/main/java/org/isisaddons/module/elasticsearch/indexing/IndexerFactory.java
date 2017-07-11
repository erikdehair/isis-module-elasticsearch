package org.isisaddons.module.elasticsearch.indexing;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

/**
 * Created by erik on 6/22/17.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public interface IndexerFactory {
    Indexer createIndexer(Indexable indexable) throws Exception;
}
