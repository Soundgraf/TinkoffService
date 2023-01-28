package com.soundgraf.tinkoffservice.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TickersDto {
    private List<String> tickers;
}
