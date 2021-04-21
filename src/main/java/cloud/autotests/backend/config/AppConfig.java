package cloud.autotests.backend.config;

import cloud.autotests.backend.services.GithubService;
import cloud.autotests.backend.services.JenkinsService;
import cloud.autotests.backend.services.JiraService;
import cloud.autotests.backend.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Autowired
    JiraConfig jiraConfig;
    @Autowired
    GithubConfig githubConfig;
    @Autowired
    JenkinsConfig jenkinsConfig;
    @Autowired
    TelegramConfig telegramConfig;

    @Bean
    JiraService jiraService() {
        return new JiraService(jiraConfig);
    }

    @Bean
    GithubService githubService() {
        return new GithubService(githubConfig);
    }

    @Bean
    JenkinsService jenkinsService() {
        return new JenkinsService(jenkinsConfig, telegramConfig);
    }

    @Bean
    TelegramService telegramService() {
        return new TelegramService(telegramConfig);
    }

}
