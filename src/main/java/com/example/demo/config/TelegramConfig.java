package com.example.demo.config;

import com.example.demo.services.TelegramService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TelegramConfig {
    @Value("${telegram.token}")
    public String telegramToken;

    @Value("${telegram.chat.id}")
    public String telegramChatId;
}
