package de.kkottke.stocktrading.generator;

import de.kkottke.stocktrading.common.model.Quote;

public interface GeneratorStrategy {

    Quote generateNext(Quote quote);
}
