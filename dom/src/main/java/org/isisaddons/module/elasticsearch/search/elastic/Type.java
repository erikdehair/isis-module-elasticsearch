package org.isisaddons.module.elasticsearch.search.elastic;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import java.util.Arrays;

public enum Type {
    empty_choice("Geen voorkeur"),
    subscription("Abonnement"),
    contact("Contactpersoon"),
    company("Klant"),
    order("Order"),
    porting("Portering"),
    phonenumber("Telefoonnummer");

    private final String typeName;

    public String getTypeName() {
        return typeName;
    }

    private Type(String typeName) {
        this.typeName = typeName;
    }

    public static String[] toArray() {
        return Iterables.toArray(Iterables.transform(Arrays.asList(values()), new Function<Type, String>() {
                    @Override
                    public String apply(Type input) {
                        return input.name();
                    }
                }),
                String.class);
    }

    public String toString() {
        return this.typeName;
    }
}