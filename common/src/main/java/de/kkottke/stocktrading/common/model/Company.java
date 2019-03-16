package de.kkottke.stocktrading.common.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Company {

    AWESOME_PRODUCTS_CORP("Awesome Products Corp.", "APC", 800, 60),
    TECH_AND_MORE_INC("Tech & More Inc.", "TMI", 500, 40),
    CRYPTO_CURRENCY_CONSULTING("Crypto Currency Consulting", "CCC", 2000, 500);

    private String name;
    private String symbol;
    private int price;
    private int variation;

}
