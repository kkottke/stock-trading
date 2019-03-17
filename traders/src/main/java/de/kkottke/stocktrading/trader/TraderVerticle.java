package de.kkottke.stocktrading.trader;

import de.kkottke.stocktrading.common.BaseVerticle;
import de.kkottke.stocktrading.common.model.Company;
import de.kkottke.stocktrading.common.model.Quote;
import de.kkottke.stocktrading.common.model.TradingEvent;
import de.kkottke.stocktrading.trading.reactivex.TradingService;
import io.reactivex.Completable;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.servicediscovery.types.MessageSource;
import lombok.extern.slf4j.Slf4j;

import static de.kkottke.stocktrading.common.model.Company.AWESOME_PRODUCTS_CORP;
import static de.kkottke.stocktrading.common.model.TradingAction.BUY;

@Slf4j
@SuppressWarnings("ResultOfMethodCallIgnored")
public class TraderVerticle extends BaseVerticle {

    static final String CONFIG_COMPANY = "GENERATOR_COMPANY";

    private static final String DEFAULT_COMPANY = AWESOME_PRODUCTS_CORP.name();

    private Company company;
    private TradingStrategy strategy;
    private TradingService tradingService;

    @Override
    public Completable rxStart() {
        Completable parentCompletable = super.rxStart();

        this.company = Company.valueOf(config().getString(CONFIG_COMPANY, DEFAULT_COMPANY));
        log.debug("starting TraderVerticle for company {}", company.getName());
        this.strategy = new SimpleTradingStrategy();
        // TODO try to get rxified service via service discovery
        this.tradingService = de.kkottke.stocktrading.trading.TradingService.createRxProxy(vertx.getDelegate());
        Completable registerConsumer = MessageSource.<JsonObject>rxGetConsumer(serviceDiscovery, new JsonObject().put("name", "market-data-stream"))
            .map(consumer -> consumer.handler(this::handleQuote))
            .ignoreElement();

        return parentCompletable.andThen(registerConsumer);
    }

    private void handleQuote(Message<JsonObject> message) {
        Quote quote = Json.decodeValue(message.body().encode(), Quote.class);
        if (company.getName().equals(quote.getName())) {
            TradingEvent tradingEvent = strategy.evaluateTrade(quote);

            if (BUY == tradingEvent.getAction()) {
                buyStocks(tradingEvent, quote);
            } else {
                sellStocks(tradingEvent, quote);
            }
        }
    }

    private void buyStocks(TradingEvent tradingEvent, Quote quote) {
        log.debug("try to buy {} of {}", tradingEvent.getAmount(), quote.getName());
        tradingService.rxBuyStock(tradingEvent.getAmount(), quote.toJson())
                      .subscribe(
                          portfolio -> log.debug(
                              "purchase of {} stocks of {} succeeded, current share {}",
                              tradingEvent.getAmount(),
                              quote.getName(),
                              portfolio.getShares().get(quote.getName())),
                          error -> log.warn("purchase failed: {}", error.getMessage()));
    }

    private void sellStocks(TradingEvent tradingEvent, Quote quote) {
        log.debug("try to sell {} of {}", tradingEvent.getAmount(), quote.getName());
        tradingService.rxSellStock(tradingEvent.getAmount(), quote.toJson())
                      .subscribe(
                          portfolio -> log.debug(
                              "sale of {} stocks of {} succeeded, current share {}",
                              tradingEvent.getAmount(),
                              quote.getName(),
                              portfolio.getShares().get(quote.getName())),
                          error -> log.warn("sale failed: {}", error.getMessage()));
    }
}
