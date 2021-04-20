package cloud.autotests.backend.services;

import cloud.autotests.backend.builders.JenkinsConfigBuilder;
import cloud.autotests.backend.builders.TestBuilder;
import cloud.autotests.backend.config.GithubConfig;
import cloud.autotests.backend.config.JenkinsConfig;
import cloud.autotests.backend.config.TelegramConfig;
import cloud.autotests.backend.models.Order;
import kong.unirest.HttpResponse;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;

public class JenkinsService {
    private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);

    private final String CREATE_JOB_URL = "%s/createItem?name=%s";
    private final String LAUNCH_JOB_URL = "%s/job/%s/buildWithParameters";

    private final String jenkinsUrl;
    private final String jenkinsUsername;
    private final String jenkinsToken;
    private final TelegramConfig telegramConfig;

    @Autowired
    public JenkinsService(JenkinsConfig jenkinsConfig, TelegramConfig telegramConfig) {
        this.jenkinsUrl = jenkinsConfig.getJenkinsUrl();
        this.jenkinsUsername = jenkinsConfig.getJenkinsUsername();
        this.jenkinsToken = jenkinsConfig.getJenkinsToken();
        this.telegramConfig = telegramConfig;
    }

    public void createJob(Order order, String jiraIssueKey, String githubRepositoryUrl, Integer telegramChatMessageId) { // todo add results parser
        String body = new JenkinsConfigBuilder().getConfig(order, telegramConfig, githubRepositoryUrl, telegramChatMessageId);

        HttpResponse<String> createJobResponse = Unirest
                .post(String.format(CREATE_JOB_URL, jenkinsUrl, jiraIssueKey))
                .header("Content-Type", "text/xml; charset=utf-8")
                .basicAuth(this.jenkinsUsername, this.jenkinsToken)
                .body(body).asString();

        LOG.error(createJobResponse.getBody());
    }

    public Integer launchJob(String jiraIssueKey) {
        HttpResponse<String> createJobResponse = Unirest
                .post(String.format(LAUNCH_JOB_URL, jenkinsUrl, jiraIssueKey))
                .header("Content-Type", "text/xml; charset=utf-8")
                .basicAuth(this.jenkinsUsername, this.jenkinsToken)
                .asString();

        LOG.error(createJobResponse.getBody());
        return null;
    }


}
