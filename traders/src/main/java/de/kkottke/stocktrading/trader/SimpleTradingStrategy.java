package de.kkottke.stocktrading.trader;

import de.kkottke.stocktrading.common.model.Quote;
import de.kkottke.stocktrading.common.model.TradingAction;
import de.kkottke.stocktrading.common.model.TradingEvent;

import java.util.Random;

import static de.kkottke.stocktrading.common.model.TradingAction.*;

public class SimpleTradingStrategy implements TradingStrategy {

    private static final int MAX_AMOUNT = 5;

    private Random random = new Random();

    @Override
    public TradingEvent evaluateTrade(Quote quote) {
        TradingAction action = random.nextBoolean() ? BUY : SELL;
        int amount = random.nextInt(MAX_AMOUNT) + 1;

        return new TradingEvent(action, quote, amount);
    }
}
