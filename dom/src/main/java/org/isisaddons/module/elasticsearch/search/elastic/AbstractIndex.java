package org.isisaddons.module.elasticsearch.search.elastic;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractIndex {
    protected static final String TITLE_JOINER = ";";

    @Getter
    @Setter
    private String tenancy;

    @Getter
    @Setter
    private String company;

    public abstract String display();

    /**
     * Returns the type of the object in the ElasticSearch index
     *
     * @return
     */
    public abstract Type getType();

    public abstract String createJson();
}
