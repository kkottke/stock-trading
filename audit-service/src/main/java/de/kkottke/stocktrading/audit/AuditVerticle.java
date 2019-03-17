package de.kkottke.stocktrading.audit;

import de.kkottke.stocktrading.common.BaseVerticle;
import de.kkottke.stocktrading.common.model.Quote;
import de.kkottke.stocktrading.common.model.TradingEvent;
import io.reactivex.Completable;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.servicediscovery.types.MessageSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditVerticle extends BaseVerticle {

    @Override
    public Completable rxStart() {
        log.debug("starting AuditVerticle");
        Completable parentCompletable = super.rxStart();

        Completable quoteConsumer = MessageSource.<JsonObject>rxGetConsumer(serviceDiscovery, new JsonObject().put("name", "market-data-stream"))
            .map(consumer -> consumer.handler(this::handleQuote))
            .ignoreElement();
        Completable tradingConsumer = MessageSource.<String>rxGetConsumer(serviceDiscovery, new JsonObject().put("name", "trading-event-stream"))
            .map(consumer -> consumer.handler(this::handleTrade))
            .ignoreElement();

        return parentCompletable.andThen(Completable.mergeArray(quoteConsumer, tradingConsumer));
    }

    private void handleQuote(Message<JsonObject> message) {
        Quote quote = Json.decodeValue(message.body().encode(), Quote.class);
        log.info("receive quote: {}", Json.encode(quote));
    }

    private void handleTrade(Message<String> message) {
        TradingEvent trade = Json.decodeValue(message.body(), TradingEvent.class);
        log.info("receive trade: {}", Json.encode(trade));
    }
}
