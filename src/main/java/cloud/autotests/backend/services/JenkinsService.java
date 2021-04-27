package cloud.autotests.backend.services;

import cloud.autotests.backend.config.JenkinsConfig;
import cloud.autotests.backend.config.TelegramConfig;
import cloud.autotests.backend.generators.jenkins.JenkinsConfigGenerator;
import cloud.autotests.backend.models.Order;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static cloud.autotests.backend.config.JenkinsConfig.CREATE_JOB_URL;
import static cloud.autotests.backend.config.JenkinsConfig.LAUNCH_JOB_URL;
import static java.lang.String.format;

@Service
@AllArgsConstructor
public class JenkinsService {
    private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);

    private final JenkinsConfig jenkinsConfig;

    private final JenkinsConfigGenerator jenkinsConfigGenerator;

    public void createJob(Order order, String jiraIssueKey, String githubRepositoryUrl, Integer telegramChatMessageId) { // todo add results parser
        String body = jenkinsConfigGenerator.getConfig(order, githubRepositoryUrl, telegramChatMessageId);
        String url = format(CREATE_JOB_URL, jenkinsConfig.getUrl(), jiraIssueKey);

        HttpResponse<String> createJobResponse = Unirest
                .post(url)
                .header("Content-Type", "text/xml; charset=utf-8")
                .basicAuth(jenkinsConfig.getUsername(), jenkinsConfig.getToken())
                .body(body).asString();

        LOG.error(createJobResponse.getBody());
    }

    public Integer launchJob(String jiraIssueKey) {
        String url = format(LAUNCH_JOB_URL, jenkinsConfig.getUrl(), jiraIssueKey);

        HttpResponse<String> createJobResponse = Unirest
                .post(url)
                .header("Content-Type", "text/xml; charset=utf-8")
                .basicAuth(jenkinsConfig.getUsername(), jenkinsConfig.getToken())
                .asString();

        LOG.error(createJobResponse.getBody());
        return null; // todo add exception or move to github actions
    }


}
