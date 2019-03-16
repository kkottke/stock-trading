package de.kkottke.stocktrading.trader;

import de.kkottke.stocktrading.common.model.Quote;
import de.kkottke.stocktrading.common.model.TradingEvent;

public interface TradingStrategy {

    TradingEvent evaluateTrade(Quote quote);
}
