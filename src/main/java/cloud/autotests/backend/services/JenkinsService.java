package cloud.autotests.backend.services;

import cloud.autotests.backend.config.JenkinsConfig;
import cloud.autotests.backend.generators.jenkins.JenkinsConfigGenerator;
import cloud.autotests.backend.models.Order;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static cloud.autotests.backend.config.JenkinsConfig.*;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

@Service
@AllArgsConstructor
public class JenkinsService {
    private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);

    private final JenkinsConfig jenkinsConfig;
    private final JenkinsConfigGenerator jenkinsConfigGenerator;

    public String createJob(Order order, String jiraIssueKey, String githubRepositoryUrl, Integer telegramChatMessageId) { // todo add results parser
        String body = jenkinsConfigGenerator.getConfig(order, githubRepositoryUrl, telegramChatMessageId);
        String createJobUrl = format(API_CREATE_JOB_URL_TEMPLATE, jenkinsConfig.getUrl(), jiraIssueKey);
        String jobUrl = format(JOB_URL_TEMPLATE, jenkinsConfig.getUrl(), jiraIssueKey);

        HttpResponse<String> createJobResponse = Unirest
                .post(createJobUrl)
                .header("Content-Type", "text/xml; charset=utf-8")
                .basicAuth(jenkinsConfig.getUsername(), jenkinsConfig.getToken())
                .body(body).asString();

        LOG.error(createJobResponse.getBody());
        return jobUrl;
    }

    public String launchJob(String jiraIssueKey) {
        String launchJobUrl = format(API_LAUNCH_JOB_URL_TEMPLATE, jenkinsConfig.getUrl(), jiraIssueKey);
        String jobUrl = format(JOB_URL_TEMPLATE, jenkinsConfig.getUrl(), jiraIssueKey);

        HttpResponse<String> createJobResponse = Unirest
                .post(launchJobUrl)
                .header("Content-Type", "text/xml; charset=utf-8")
                .basicAuth(jenkinsConfig.getUsername(), jenkinsConfig.getToken())
                .asString();

        LOG.error(createJobResponse.getBody());
        return jobUrl; // todo add exception or move to github actions
    }


    public void awaitJobFinished(String jiraIssueKey) {
        Awaitility.await().atMost(60, SECONDS)
                .pollInterval(3, SECONDS)
                .until(() -> isJobFinished(jiraIssueKey));
    }

    public boolean isJobFinished(String jiraIssueKey) {
        String jobStatusUrl = format(API_JOB_STATUS_URL_TEMPLATE, jenkinsConfig.getUrl(), jiraIssueKey);
        JSONObject a = Unirest.get(jobStatusUrl)
                .asJson().getBody().getObject();
        return !a.isNull("result");
    }
}
