package de.kkottke.stocktrading.trading;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@DataObject(generateConverter = true)
public class Portfolio {

    private double cash;
    private double value;
    private Map<String, Integer> shares = new HashMap<>();

    public Portfolio(JsonObject jsonObject) {
        PortfolioConverter.fromJson(jsonObject, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        PortfolioConverter.toJson(this, json);
        return json;
    }
}
