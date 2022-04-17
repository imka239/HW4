package ru.gnatyuk.sd.server.config;

import ru.gnatyuk.sd.server.stock.StockModel;

import java.util.Random;

public class RandomMarketState {
    private final StockModel stockDao;
    private final Random random = new Random(4);

    public RandomMarketState(final StockModel stockDao) {
        this.stockDao = stockDao;
    }

    public void updateState() {
        this.stockDao.getAllStocks().forEach(stock -> this.stockDao.changeStock(
                stock.getName(),
                stock.getCorpName(),
                0,
                random.nextDouble()
        ));
    }
}
