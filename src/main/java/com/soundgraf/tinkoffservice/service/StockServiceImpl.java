package com.soundgraf.tinkoffservice.service;

import com.soundgraf.tinkoffservice.dto.FigiesDto;
import com.soundgraf.tinkoffservice.dto.StockPrice;
import com.soundgraf.tinkoffservice.dto.StocksDto;
import com.soundgraf.tinkoffservice.dto.StocksPricesDto;
import com.soundgraf.tinkoffservice.dto.TickersDto;
import com.soundgraf.tinkoffservice.exception.StockNotFoundException;
import com.soundgraf.tinkoffservice.model.Currency;
import com.soundgraf.tinkoffservice.model.Stock;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrumentList;
import ru.tinkoff.invest.openapi.model.rest.Orderbook;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final OpenApi api;

    @Override
    public Stock getStockByTicker(String ticker) {
        var cf = getMarketInstrumentTicker(ticker);
        var list = cf.join().getInstruments();
        if (list.isEmpty()) {
            throw new StockNotFoundException(String.format("Stock %S not found.", ticker));
        }
        var item = list.get(0);
        return new Stock(
            item.getTicker(),
            item.getFigi(),
            item.getName(),
            item.getType().getValue(),
            Currency.valueOf(item.getCurrency().getValue()), "TINKOFF");
    }
    @Override
    public StocksDto getStocksByTickers(TickersDto tickers) {
        List<CompletableFuture<MarketInstrumentList>> marketInstruments = new ArrayList<>();
        tickers.getTickers().forEach(ticker -> marketInstruments.add(getMarketInstrumentTicker(ticker)));
        List<Stock> stocks = marketInstruments.stream()
            .map(CompletableFuture::join)
            .map(mi -> {
                if (!mi.getInstruments().isEmpty()) {
                    return mi.getInstruments().get(0);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .map(mi -> new Stock(
                mi.getTicker(),
                mi.getFigi(),
                mi.getName(),
                mi.getType().getValue(),
                Currency.valueOf(mi.getCurrency().getValue()),
                "TINKOFF"))
            .collect(Collectors.toList());
        return new StocksDto(stocks);
    }

    @Override
    public StocksPricesDto getPricesStocksByFigies(FigiesDto figiesDto) {
        long start = System.currentTimeMillis();
        List<CompletableFuture<Optional<Orderbook>>> orderBooks = new ArrayList<>();
        figiesDto.getFigies().forEach(figi -> orderBooks.add(getOrderBookByFigi(figi)));
        List<StockPrice> prices =  orderBooks.stream()
            .map(CompletableFuture::join)
            .map(oo -> oo.orElseThrow(() -> new StockNotFoundException("Stock not found.")))
            .map(orderBook -> new StockPrice(
                orderBook.getFigi(),
                orderBook.getLastPrice().doubleValue())).collect(Collectors.toList());

        log.info("Time getting prices - {}", System.currentTimeMillis() - start);
        return new StocksPricesDto(prices);
    }

    @Async
    public CompletableFuture<MarketInstrumentList> getMarketInstrumentTicker(String ticker) {
        log.info("Getting {} from Tinkoff", ticker);
        var context = api.getMarketContext();
        return context.searchMarketInstrumentsByTicker(ticker);
    }

    @Async
    public CompletableFuture<Optional<Orderbook>> getOrderBookByFigi(String figi) {
        var orderBook = api.getMarketContext().getMarketOrderbook(figi, 0);
        log.info("Getting price {} from Tinkoff", figi);
        return orderBook;
    }
}
