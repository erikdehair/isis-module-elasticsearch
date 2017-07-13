package org.isisaddons.module.elasticsearch.fixture.dom;

import lombok.Getter;
import lombok.Setter;
import org.isisaddons.module.elasticsearch.indexing.AbstractIndex;

/**
 * Created by E. de Hair <e.dehair@pocos.nl> on 6/18/17.
 */
public class AnotherElasticSearchDemoObjectIndex extends AbstractIndex<AnotherElasticSearchDemoObject> {

    @Override
    public Class<AnotherElasticSearchDemoObject> getType() {
        return AnotherElasticSearchDemoObject.class;
    }

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String remarks;
}
