package cloud.autotests.backend.config;

import cloud.autotests.backend.services.TelegramService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    TelegramService telegramService() {
        return new TelegramService();
    }
}
