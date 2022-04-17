package ru.gnatyuk.sd.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gnatyuk.sd.client.client.ClientModel;
import ru.gnatyuk.sd.client.client.ClientModelImpl;
import ru.gnatyuk.sd.client.stock.StockClient;

@Configuration
public class ClientConfig {
    @Bean
    public ClientModel clientDao(final StockClient stockClient) {
        return new ClientModelImpl(stockClient);
    }
}
