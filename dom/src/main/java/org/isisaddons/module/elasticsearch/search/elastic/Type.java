package org.isisaddons.module.elasticsearch.search.elastic;

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