package de.kkottke.stocktrading.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;

public class CustomDateDeserializer extends StdDeserializer<ZonedDateTime> implements CustomDeSer {

    public CustomDateDeserializer() {
        this(null);
    }

    public CustomDateDeserializer(Class<ZonedDateTime> clazz) {
        super(clazz);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String date = jsonParser.getText();
        return ZonedDateTime.parse(date, formatter);
    }
}
