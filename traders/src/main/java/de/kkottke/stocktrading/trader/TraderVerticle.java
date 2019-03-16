package de.kkottke.stocktrading.trader;

import de.kkottke.stocktrading.common.BaseVerticle;
import de.kkottke.stocktrading.common.model.Company;
import de.kkottke.stocktrading.common.model.Quote;
import io.reactivex.Completable;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.servicediscovery.types.MessageSource;
import lombok.extern.slf4j.Slf4j;

import static de.kkottke.stocktrading.common.model.Company.AWESOME_PRODUCTS_CORP;

@Slf4j
public class TraderVerticle extends BaseVerticle {

    static final String CONFIG_COMPANY = "GENERATOR_COMPANY";

    private static final String DEFAULT_COMPANY = AWESOME_PRODUCTS_CORP.name();

    private Company company;
    private TradingStrategy strategy;

    @Override
    public Completable rxStart() {
        log.debug("starting TraderVerticle");
        Completable parentCompletable = super.rxStart();

        this.company = Company.valueOf(config().getString(CONFIG_COMPANY, DEFAULT_COMPANY));
        this.strategy = new SimpleTradingStrategy();
        Completable registerConsumer = MessageSource.<String>rxGetConsumer(serviceDiscovery, new JsonObject().put("name", "market-data-stream"))
                                                    .map(consumer -> consumer.handler(this::handleQuote))
                                                    .ignoreElement();

        return parentCompletable.andThen(registerConsumer);
    }

    private void handleQuote(Message<String> message) {
        Quote quote = Json.decodeValue(message.body(), Quote.class);
        log.info("receive quote: {}", Json.encode(quote));
    }
}
