package ru.gnatyuk.sd.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gnatyuk.sd.server.stock.StockModelImpl;
import ru.gnatyuk.sd.server.stock.StockModel;

@Configuration
public class StockConfiguration {
    @Bean
    public StockModel stockDao() {
        return new StockModelImpl();
    }
}
