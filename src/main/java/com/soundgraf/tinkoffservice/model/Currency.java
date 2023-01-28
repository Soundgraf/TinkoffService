package com.soundgraf.tinkoffservice.model;

public enum Currency {
    RUB("RUN"),
    USD("USD"),
    EUR("EUR"),
    GBR("GBR"),
    HKO("HKO"),
    CHF("CHF"),
    JPY("JPY"),
    CNY("CNY"),
    TRY("TRY");
    private String currency;

    Currency(String currency) {
        this.currency = currency;
    }
}
