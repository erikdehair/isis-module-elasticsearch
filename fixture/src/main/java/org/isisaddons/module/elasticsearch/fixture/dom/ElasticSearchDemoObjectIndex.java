package org.isisaddons.module.elasticsearch.fixture.dom;

import lombok.Getter;
import lombok.Setter;
import org.isisaddons.module.elasticsearch.search.elastic.Type;
import org.isisaddons.module.elasticsearch.search.elastic.indexing.AbstractIndex;

/**
 * Created by E. de Hair <e.dehair@pocos.nl> on 6/18/17.
 */
public class ElasticSearchDemoObjectIndex extends AbstractIndex {

    public final static Type type = Type.company;

    @Override
    public Type getType()
    {
        return type;
    }

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;
}
