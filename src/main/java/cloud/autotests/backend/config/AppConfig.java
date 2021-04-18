package cloud.autotests.backend.config;

import cloud.autotests.backend.services.GithubService;
import cloud.autotests.backend.services.JiraService;
import cloud.autotests.backend.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Autowired
    JiraConfig jiraConfig;

    @Bean
    JiraService jiraService() {
        return new JiraService(jiraConfig);
    }

    @Autowired
    GithubConfig githubConfig;

    @Bean
    GithubService githubService() {
        return new GithubService(githubConfig);
    }

    @Autowired
    TelegramConfig telegramConfig;

    @Bean
    TelegramService telegramService() {
        return new TelegramService(telegramConfig);
    }

}
