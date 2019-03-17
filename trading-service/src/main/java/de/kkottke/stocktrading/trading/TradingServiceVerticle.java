package de.kkottke.stocktrading.trading;

import de.kkottke.stocktrading.common.model.Quote;
import de.kkottke.stocktrading.common.model.TradingEvent;
import io.reactivex.Maybe;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.slf4j.Slf4j;

import static de.kkottke.stocktrading.common.model.TradingAction.BUY;
import static de.kkottke.stocktrading.common.model.TradingAction.SELL;
import static org.apache.http.HttpStatus.SC_OK;

@Slf4j
public class TradingServiceVerticle extends AbstractVerticle implements TradingService {

    private static final String CONFIG_CASH = "TRADING_INITIAL_CASH";

    private static final double DEFAULT_CASH = 100000;

    private Portfolio portfolio;
    private ServiceDiscovery serviceDiscovery;

    public TradingServiceVerticle(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public void start(Future<Void> startFuture) {
        log.debug("starting TradingServiceVerticle");
        this.portfolio = new Portfolio();
        this.portfolio.setCash(config().getDouble(CONFIG_CASH, DEFAULT_CASH));

        // Register this service for 'TradingService' interface
        new ServiceBinder(vertx.getDelegate()).setAddress(SERVICE_ADDRESS).register(TradingService.class, this);
        startFuture.complete();
    }

    @Override
    public TradingService buyStock(int amount, JsonObject json, Handler<AsyncResult<Portfolio>> resultHandler) {
        if (amount <= 0) {
            resultHandler.handle(Future.failedFuture("amount must be greater than 0"));
        }

        // TODO check volume

        Quote quote = Json.decodeValue(json.encode(), Quote.class);
        double price = amount * quote.getPrice();
        String name = quote.getName();
        if (portfolio.getCash() >= price) {
            portfolio.setCash(portfolio.getCash() - price);
            int currentAmount = portfolio.getShares().getOrDefault(name, 0);
            portfolio.getShares().put(name, currentAmount + amount);

            log.info("buy {} stocks of {} worth {}", amount, name, price);
            publishTradingEvent(new TradingEvent(BUY, quote, amount));

            // TODO update portfolio value
            resultHandler.handle(Future.succeededFuture(portfolio));
        } else {
            String message = String.format("reject purchase request: insufficient cash %s (cash) < %s (price)", portfolio.getCash(), price);
            log.warn(message);
            resultHandler.handle(Future.failedFuture(message));
        }

        return this;
    }

    @Override
    public TradingService sellStock(int amount, JsonObject json, Handler<AsyncResult<Portfolio>> resultHandler) {
        if (amount <= 0) {
            resultHandler.handle(Future.failedFuture("amount must be greater than 0"));
        }

        Quote quote = Json.decodeValue(json.encode(), Quote.class);
        double price = amount * quote.getPrice();
        String name = quote.getName();
        int currentAmount = portfolio.getShares().getOrDefault(name, 0);
        if (currentAmount >= amount) {
            int newAmount = currentAmount - amount;
            if (newAmount == 0) {
                portfolio.getShares().remove(name);
            } else {
                portfolio.getShares().put(name, newAmount);
            }
            portfolio.setCash(portfolio.getCash() + price);

            log.info("sell {} stocks of {} worth {}", amount, name, price);
            publishTradingEvent(new TradingEvent(SELL, quote, amount));

            // TODO update portfolio value
            resultHandler.handle(Future.succeededFuture(portfolio));
        } else {
            String message = String.format("reject sale request: insufficient stocks %s (owned) < %s (amount)", currentAmount, amount);
            log.warn(message);
            resultHandler.handle(Future.failedFuture(message));
        }

        return this;
    }

    @Override
    public TradingService evaluatePortfolie(Handler<AsyncResult<Portfolio>> resultHandler) {
        throw new IllegalStateException("not implemented yet");
    }

    private void publishTradingEvent(TradingEvent event) {
        vertx.eventBus().publish(EVENT_ADDRESS, Json.encode(event));
    }

    private Maybe<JsonObject> getCurrentQuote(String stock) {
        return HttpEndpoint.rxGetWebClient(serviceDiscovery, new JsonObject().put("name", "market-data-api"))
                           .flatMap(client -> client.get("/" + stock).as(BodyCodec.jsonObject()).rxSend())
                           .filter(response -> response.statusCode() == SC_OK)
                           .map(HttpResponse::body);
    }
}
