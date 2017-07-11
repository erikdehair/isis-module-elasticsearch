package org.isisaddons.module.elasticsearch.fixture.dom;

import lombok.Getter;
import lombok.Setter;
import org.isisaddons.module.elasticsearch.indexing.AbstractIndex;

/**
 * Created by E. de Hair <e.dehair@pocos.nl> on 6/18/17.
 */
public class ElasticSearchDemoObjectIndex extends AbstractIndex<ElasticSearchDemoObject> {

    @Override
    public Class<ElasticSearchDemoObject> getType() {
        return ElasticSearchDemoObject.class;
    }

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;
}
