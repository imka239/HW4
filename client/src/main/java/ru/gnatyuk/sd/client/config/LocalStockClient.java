package ru.gnatyuk.sd.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gnatyuk.sd.client.stock.StockClient;

@Configuration
public class LocalStockClient {
    @Bean
    public StockClient stockClient() {
        return new StockClient("http://127.0.0.1:8080");
    }
}
