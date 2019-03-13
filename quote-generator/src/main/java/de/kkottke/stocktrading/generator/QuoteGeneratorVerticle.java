package de.kkottke.stocktrading.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.kkottke.stocktrading.common.model.Quote;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;

import static de.kkottke.stocktrading.generator.Company.AWESOME_PRODUCTS_CORP;

@Slf4j
public class QuoteGeneratorVerticle extends AbstractVerticle {

    public static final String ADDRESS = "market-data";
    public static final String CONFIG_DELAY = "generator.delay";
    public static final String CONFIG_COMPANY = "generator.company";

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
        this.quote = new Quote(company.getName(), company.getPrice(), ZonedDateTime.now());

        log.info("start quote generator for company {}", company.getName());
        vertx.setPeriodic(config().getLong(CONFIG_DELAY, DEFAULT_DELAY), handler -> {
            quote = strategy.generateNext(quote);
            publishQuote(quote);
        });
    }

    private void publishQuote(Quote quote) {
        log.debug("publish new quote for {} on the event bus", quote.getName());
        eventBus.publish(ADDRESS, Json.encode(quote));
    }
}
