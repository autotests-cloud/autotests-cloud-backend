package com.example.demo.config;

import com.example.demo.services.TelegramService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    TelegramService telegramService() {
        return new TelegramService();
    }
}
