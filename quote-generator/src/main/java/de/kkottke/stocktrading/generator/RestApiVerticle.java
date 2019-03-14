package de.kkottke.stocktrading.generator;

import de.kkottke.stocktrading.common.model.Quote;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;

@Slf4j
public class RestApiVerticle extends AbstractVerticle {

    static final int DEFAULT_PORT = 8080;
    private static final String PORT = "HTTP_PORT";

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
            quotes.put(quote.getSymbol(), quote);
        });
    }

    private Completable startServer() {
        final int port = config().getInteger(PORT, DEFAULT_PORT);
        Single<Router> routerSingle = OpenAPI3RouterFactory.rxCreate(vertx, "api/swagger.yaml").map(routerFactory -> {
            registerOperations(routerFactory);
            return routerFactory.getRouter();
        });

        return routerSingle.flatMap(router -> vertx.createHttpServer().requestHandler(router).rxListen(port))
                           .ignoreElement()
                           .doOnComplete(() -> log.info("rest api is listening on {}", port));
    }

    private void registerOperations(OpenAPI3RouterFactory routerFactory) {
        routerFactory.addHandlerByOperationId("getQuotes", this::handleGetQuotes);
        routerFactory.addHandlerByOperationId("getQuote", this::handleGetQuote);
    }

    private void handleGetQuotes(RoutingContext context) {
        context.response()
               .putHeader("content-type", "application/json")
               .setStatusCode(SC_OK)
               .end(Json.encodePrettily(quotes));
    }

    private void handleGetQuote(RoutingContext context) {
        String stock = context.request().getParam("stock");
        HttpServerResponse response = context.response()
                                             .putHeader("content-type", "application/json");
        if (quotes.containsKey(stock)) {
            response.setStatusCode(SC_OK)
                    .end(Json.encodePrettily(quotes.get(stock)));
        } else {
            response.setStatusCode(SC_NOT_FOUND).end();
        }
    }
}
