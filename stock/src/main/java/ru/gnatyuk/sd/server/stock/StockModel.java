package ru.gnatyuk.sd.server.stock;

import java.util.List;

public interface StockModel {
    void addStock(final Stock stock);

    Stock getStock(final String corpName, final String name);

    List<Stock> getAllStocks();

    double changeStock(final String name, final String corpName, final long quantityDelta, final double priceDelta);
}
