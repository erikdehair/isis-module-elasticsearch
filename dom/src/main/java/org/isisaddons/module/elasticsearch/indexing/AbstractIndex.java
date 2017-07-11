package org.isisaddons.module.elasticsearch.indexing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.Programmatic;
import org.isisaddons.module.elasticsearch.util.JsonSerializers;
import org.joda.time.LocalDate;

public abstract class AbstractIndex<T extends Indexable> {
    /**
     * A tenancy can be used to only retreive items the user has access to.
     */
    @Getter
    @Setter
    private String tenancy;

    /**
     * Returns the type of the object in the ElasticSearch index.
     *
     * @return
     */
    public abstract Class<T> getType();

    /**
     * This JSON string will be fed to Elastic Search indexing. Each item in the JSON string will be added to the
     * internal representation of the indexed object and be indexed.
     * @return
     */
    @Programmatic
    public String createJson() {
        return createGsonBuilder().toJson(this);
    }

    private static Gson createGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonSerializers.LocalDateDeserializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonSerializers.LocalDateSerializer());
        return gsonBuilder.create();
    }
}
