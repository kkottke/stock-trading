package de.kkottke.stocktrading.generator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Company {

    AWESOME_PRODUCTS_CORP("Awesome Products Corp.", 5200, 100),
    TECH_AND_MORE_INC("Tech & More Inc.", 500, 200),
    CRYPTO_CURRENCY_CONSULTING("Crypto Currency Consulting", 8000, 500);

    private String name;
    private int price;
    private int variation;

}
