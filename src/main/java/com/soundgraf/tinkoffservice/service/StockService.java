package com.soundgraf.tinkoffservice.service;

import com.soundgraf.tinkoffservice.dto.FigiesDto;
import com.soundgraf.tinkoffservice.dto.StocksDto;
import com.soundgraf.tinkoffservice.dto.StocksPricesDto;
import com.soundgraf.tinkoffservice.dto.TickersDto;
import com.soundgraf.tinkoffservice.model.Stock;

public interface StockService {
    Stock getStockByTicker(String ticker);

    StocksPricesDto getPricesStocksByFigies(FigiesDto figiesDto);

    StocksDto getStocksByTickers(TickersDto tickers);
}
