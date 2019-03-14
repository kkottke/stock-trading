package de.kkottke.stocktrading.trading;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface TradingService {

    String EVENT_ADDRESS = "trading-event-stream";
    String SERVICE_ADDRESS = "trading-service";

    @GenIgnore
    static TradingService createProxy(Vertx vertx) {
        return new TradingServiceVertxEBProxy(vertx, SERVICE_ADDRESS);
    }

    @GenIgnore
    static de.kkottke.stocktrading.trading.reactivex.TradingService createRxProxy(Vertx vertx) {
        return new de.kkottke.stocktrading.trading.reactivex.TradingService(createProxy(vertx));
    }

    @Fluent
    TradingService buyStock(int amount, JsonObject quote, Handler<AsyncResult<Portfolio>> resultHandler);

    @Fluent
    TradingService sellStock(int amount, JsonObject quote, Handler<AsyncResult<Portfolio>> resultHandler);

    @Fluent
    TradingService evaluatePortfolie(Handler<AsyncResult<Portfolio>> resultHandler);
}
