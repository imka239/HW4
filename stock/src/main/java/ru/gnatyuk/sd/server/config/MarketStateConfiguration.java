package ru.gnatyuk.sd.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gnatyuk.sd.server.stock.StockModel;

@Configuration
public class MarketStateConfiguration {
    @Bean
    public RandomMarketState marketState(final StockModel stockDao) {
        return new RandomMarketState(stockDao);
    }
}
