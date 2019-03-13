package de.kkottke.stocktrading.generator;

import de.kkottke.stocktrading.common.model.Quote;
import io.reactivex.Completable;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RestApiVerticle extends AbstractVerticle {

    public static final int DEFAULT_PORT = 8080;

    private Map<String, Quote> quotes = new HashMap<>();

    @Override
    public Completable rxStart() {
        log.debug("starting RestApiVerticle");
        registerConsumer();

        return startServer();
    }

    private void registerConsumer() {
        vertx.eventBus().<String>consumer(QuoteGeneratorVerticle.ADDRESS, message -> {
            Quote quote = Json.decodeValue(message.body(), Quote.class);
            quotes.put(quote.getName(), quote);
        });
    }

    private Completable startServer() {
        return vertx.createHttpServer()
                    .requestHandler(request -> request.response().end(Json.encodePrettily(quotes)))
                    .rxListen(config().getInteger("HTTP_PORT", DEFAULT_PORT))
                    .ignoreElement()
                    .doOnComplete(() -> log.info("rest api is listening"));
    }
}
