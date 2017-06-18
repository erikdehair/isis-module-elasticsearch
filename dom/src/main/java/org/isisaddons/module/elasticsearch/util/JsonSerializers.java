/**
 *
 */
package org.isisaddons.module.elasticsearch.util;

import com.google.gson.*;
import org.joda.time.LocalDate;

import java.lang.reflect.Type;

/**
 * @author "Erik de Hair <erik@pocos.nl>"
 */
public class JsonSerializers {
    public static class LocalDateSerializer implements JsonSerializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext jsc) {
            JsonObject jo = new JsonObject();
            jo.addProperty("value", date != null ? date.toString("yyyy-MM-dd") : "");
            return jo;
        }
    }

    public static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            JsonObject jo = je.getAsJsonObject();
            if (jo.getAsJsonPrimitive("value") != null) {
                LocalDate date = LocalDate.parse(jo.getAsJsonPrimitive("value").getAsString());
                return date;
            } else {
                return null;
            }
        }
    }
}
