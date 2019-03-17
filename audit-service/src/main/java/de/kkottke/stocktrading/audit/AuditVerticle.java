package de.kkottke.stocktrading.audit;

import de.kkottke.stocktrading.common.BaseVerticle;
import de.kkottke.stocktrading.common.model.TradingEvent;
import io.reactivex.Completable;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.servicediscovery.types.MessageSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditVerticle extends BaseVerticle {

    private static final String DEFAULT_ES_HOST = "localhost";
    private static final int DEFAULT_ES_PORT = 9200;

    private WebClient webClient;

    @Override
    public Completable rxStart() {
        log.debug("starting AuditVerticle");
        Completable parentCompletable = super.rxStart();

        webClient = WebClient.create(vertx, new WebClientOptions().setDefaultHost(DEFAULT_ES_HOST).setDefaultPort(DEFAULT_ES_PORT));

        Completable quoteConsumer = MessageSource.<JsonObject>rxGetConsumer(serviceDiscovery, new JsonObject().put("name", "market-data-stream"))
            .map(consumer -> consumer.handler(this::handleQuote))
            .ignoreElement();
        Completable tradingConsumer = MessageSource.<String>rxGetConsumer(serviceDiscovery, new JsonObject().put("name", "trading-event-stream"))
            .map(consumer -> consumer.handler(this::handleTrade))
            .ignoreElement();

        return parentCompletable.andThen(Completable.mergeArray(quoteConsumer, tradingConsumer));
    }

    private void handleQuote(Message<JsonObject> message) {
        webClient.post("audit/_doc/")
                 .rxSendJsonObject(message.body())
                 .doOnError(error -> log.warn("persisting quote failed: {}", error.getMessage())).subscribe();
    }

    private void handleTrade(Message<String> message) {
        TradingEvent trade = Json.decodeValue(message.body(), TradingEvent.class);
        webClient.post("trade/_doc/")
                 .rxSendJson(trade)
                 .doOnError(error -> log.warn("persisting trade failed: {}", error.getMessage())).subscribe();
    }

}
