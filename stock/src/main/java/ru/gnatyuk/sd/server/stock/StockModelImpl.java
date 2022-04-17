package ru.gnatyuk.sd.server.stock;

import java.util.*;
import java.util.stream.Collectors;

public class StockModelImpl implements StockModel {
    private final Map<String, List<Stock>> stocksByCorpName = new HashMap<>();

    private Optional<Stock> getStockOptional(final String corpName, final String stockName) {
        if (!stocksByCorpName.containsKey(corpName)) {
            return Optional.empty();
        }
        return stocksByCorpName.get(corpName).stream()
                .filter(stock -> stock.getName().equals(stockName))
                .findFirst();
    }

    @Override
    public void addStock(final Stock stock) {
        final String corpName = stock.getCorpName();
        if (getStockOptional(corpName, stock.getName()).isPresent()) {
            throw new IllegalArgumentException(
                    "Stock '" + stock.getName() + "' is already present. Consider changeing it."
            );
        }
        stocksByCorpName.putIfAbsent(corpName, new ArrayList<>());
        stocksByCorpName.get(corpName).add(stock);
    }

    @Override
    public Stock getStock(final String corpName, final String stockName) {
        final Optional<Stock> stockOptional = getStockOptional(corpName, stockName);
        if (stockOptional.isEmpty()) {
            throw new IllegalArgumentException("Stock '" + stockName + "' by '" + corpName + "' is not found.");
        }
        return stockOptional.get();
    }

    @Override
    public List<Stock> getAllStocks() {
        return stocksByCorpName.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public double changeStock(final String stockName, final String corpName,
                              final long quantityDelta, final double priceDelta) {
        final Optional<Stock> stockOptional = getStockOptional(corpName, stockName);
        if (stockOptional.isEmpty()) {
            throw new IllegalArgumentException(
                    "Stock '" + stockName + "' by '" + corpName + "' is not present, so cannot be modified."
            );
        }
        final Stock stock = stockOptional.get();
        final long newQuantity = stock.getQuantity() + quantityDelta;
        final double newPrice = stock.getPrice() + priceDelta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("New stock's quantity will be " + newQuantity + ", impossible.");
        }
        if (newPrice <= 0.0) {
            throw new IllegalArgumentException("New stock's price will be " + newPrice + ", impossible");
        }
        stockOptional.get().setQuantity(newQuantity);
        stockOptional.get().setPrice(newPrice);
        return newPrice;
    }
}
