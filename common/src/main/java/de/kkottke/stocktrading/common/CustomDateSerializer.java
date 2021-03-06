package de.kkottke.stocktrading.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.ZonedDateTime;

public class CustomDateSerializer extends StdSerializer<ZonedDateTime> implements CustomDeSer {

    public CustomDateSerializer() {
        this(null);
    }

    public CustomDateSerializer(Class<ZonedDateTime> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(formatter.format(value));
    }
}
