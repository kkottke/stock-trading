package de.kkottke.stocktrading.common.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.kkottke.stocktrading.common.CustomDateDeserializer;
import de.kkottke.stocktrading.common.CustomDateSerializer;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

import static de.kkottke.stocktrading.common.CustomDeSer.formatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quote {

    private String name;
    private String symbol;
    private double price;
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private ZonedDateTime quoteTime;

    public JsonObject toJson() {
        return new JsonObject().put("name", name)
                               .put("symbol", symbol)
                               .put("price", price).put("quoteTime", formatter.format(quoteTime));
    }
}
