package de.kkottke.stocktrading.generator;

import de.kkottke.stocktrading.common.model.Quote;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

import static de.kkottke.stocktrading.generator.Company.AWESOME_PRODUCTS_CORP;

@Slf4j
public class QuoteGeneratorVerticle extends AbstractVerticle {

    static final String ADDRESS = "market-data";
    private static final String CONFIG_DELAY = "GENERATOR_DELAY";
    static final String CONFIG_COMPANY = "GENERATOR_COMPANY";

    private static final long DEFAULT_DELAY = 2000L;
    private static final String DEFAULT_COMPANY = AWESOME_PRODUCTS_CORP.name();

    private EventBus eventBus;
    private GeneratorStrategy strategy;
    private Quote quote;

    @Override
    public void start() {
        this.eventBus = vertx.eventBus();
        Company company = Company.valueOf(config().getString(CONFIG_COMPANY, DEFAULT_COMPANY));
        this.strategy = new SimpleGeneratorStrategy(company.getVariation());
        this.quote = new Quote(company.getName(), company.getSymbol(), company.getPrice(), ZonedDateTime.now());

        long delay = config().getLong(CONFIG_DELAY, DEFAULT_DELAY);
        log.info("starting quote generator for company {} every {}ms", company.getName(), delay);
        vertx.setPeriodic(delay, handler -> {
            quote = strategy.generateNext(quote);
            publishQuote(quote);
        });
    }

    private void publishQuote(Quote quote) {
        eventBus.publish(ADDRESS, Json.encode(quote));
    }
}
