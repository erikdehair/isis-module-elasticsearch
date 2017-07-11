package org.isisaddons.module.elasticsearch.search;

import java.util.Arrays;

public enum Type {
    empty_choice("No preference"),
    subscription("Subscription"),
    contact("Contact"),
    company("Company"),
    order("Order"),
    porting("Porting"),
    phonenumber("Telefoonnummer");

    private final String typeName;

    public String getTypeName() {
        return typeName;
    }

    Type(String typeName) {
        this.typeName = typeName;
    }

    public static String[] toArray() {
        return Arrays.stream(values())
                .map(v -> v.name())
                .toArray(String[]::new);
    }

    public String toString() {
        return getTypeName();
    }
}