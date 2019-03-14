package de.kkottke.stocktrading.trading;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link de.kkottke.stocktrading.trading.Portfolio}.
 * NOTE: This class has been automatically generated from the {@link de.kkottke.stocktrading.trading.Portfolio} original class using Vert.x codegen.
 */
public class PortfolioConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Portfolio obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "cash":
          if (member.getValue() instanceof Number) {
            obj.setCash(((Number)member.getValue()).doubleValue());
          }
          break;
        case "shares":
          if (member.getValue() instanceof JsonObject) {
            java.util.Map<String, java.lang.Integer> map = new java.util.LinkedHashMap<>();
            ((Iterable<java.util.Map.Entry<String, Object>>)member.getValue()).forEach(entry -> {
              if (entry.getValue() instanceof Number)
                map.put(entry.getKey(), ((Number)entry.getValue()).intValue());
            });
            obj.setShares(map);
          }
          break;
        case "value":
          if (member.getValue() instanceof Number) {
            obj.setValue(((Number)member.getValue()).doubleValue());
          }
          break;
      }
    }
  }

  public static void toJson(Portfolio obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Portfolio obj, java.util.Map<String, Object> json) {
    json.put("cash", obj.getCash());
    if (obj.getShares() != null) {
      JsonObject map = new JsonObject();
      obj.getShares().forEach((key, value) -> map.put(key, value));
      json.put("shares", map);
    }
    json.put("value", obj.getValue());
  }
}
