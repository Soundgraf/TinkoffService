package com.soundgraf.tinkoffservice.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class StockPrice {
    String figi;
    Double price;
}
