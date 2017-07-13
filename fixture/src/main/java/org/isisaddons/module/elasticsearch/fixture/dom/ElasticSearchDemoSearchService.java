package org.isisaddons.module.elasticsearch.fixture.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.isisaddons.module.elasticsearch.indexing.Indexable;
import org.isisaddons.module.elasticsearch.search.SearchService;

import java.util.HashMap;

/**
 * Created by E. de Hair <e.dehair@pocos.nl> on 7/13/17.
 */
@DomainService(nature = NatureOfService.VIEW)
public class ElasticSearchDemoSearchService extends SearchService {

    @Override
    public HashMap<Class<? extends Indexable>, Integer> getPreferredTypes() {
        return new HashMap<Class<? extends Indexable>, Integer>() {{
            put(ElasticSearchDemoObject.class,1);
            put(AnotherElasticSearchDemoObject.class,1);
        }};
    }
}
