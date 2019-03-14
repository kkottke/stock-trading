package de.kkottke.stocktrading.trading.model;

import de.kkottke.stocktrading.common.model.Quote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradingEvent {

    private TradingAction action;
    private Quote quote;
    private int amount;
}
