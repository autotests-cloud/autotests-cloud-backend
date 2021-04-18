package cloud.autotests.backend.config;

import cloud.autotests.backend.services.JiraService;
import cloud.autotests.backend.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Autowired
    TelegramConfig telegramConfig;

    @Bean
    TelegramService telegramService() {
        return new TelegramService(telegramConfig);
    }

    @Autowired
    JiraConfig jiraConfig;

    @Bean
    JiraService jiraService() {
        return new JiraService(jiraConfig);
    }
}
