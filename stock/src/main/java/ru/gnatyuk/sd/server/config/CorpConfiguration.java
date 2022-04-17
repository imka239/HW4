package ru.gnatyuk.sd.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gnatyuk.sd.server.corp.CorpModel;
import ru.gnatyuk.sd.server.corp.CorpModelImpl;

@Configuration
public class CorpConfiguration {
    @Bean
    public CorpModel corpDao() {
        return new CorpModelImpl();
    }
}
