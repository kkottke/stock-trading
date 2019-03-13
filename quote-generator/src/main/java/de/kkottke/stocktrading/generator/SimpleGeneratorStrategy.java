package de.kkottke.stocktrading.generator;

import de.kkottke.stocktrading.common.model.Quote;

import java.time.ZonedDateTime;
import java.util.Random;

public class SimpleGeneratorStrategy implements GeneratorStrategy {

    private final int variation;
    private final Random random = new Random();

    public SimpleGeneratorStrategy(final int variation) {
        this.variation = variation;
    }

    @Override
    public Quote generateNext(Quote quote) {
        return new Quote(quote.getName(), generateNextPrice(quote.getPrice()), ZonedDateTime.now());
    }

    private double generateNextPrice(double price) {
        double nextPrice = price;
        if (random.nextBoolean()) {
            // increase price
            nextPrice += random.nextInt(variation);
        } else {
            // decrease price
            nextPrice -= random.nextInt(variation);
        }

        return nextPrice;
    }
}
