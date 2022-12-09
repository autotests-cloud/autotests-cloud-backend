package cloud.autotests.backend.services;

import cloud.autotests.backend.config.JenkinsConfig;
import cloud.autotests.backend.generators.jenkins.JenkinsConfigGenerator;
import cloud.autotests.backend.models.request.Opts;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import static cloud.autotests.backend.config.JenkinsConfig.*;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

@Service
@RequiredArgsConstructor
@Slf4j
public class JenkinsService {

    private final JenkinsConfig jenkinsConfig;
    private final JenkinsConfigGenerator jenkinsConfigGenerator;

    public String createJob(@NotNull Opts opts, String jiraIssueKey, String githubRepositoryUrl, Integer telegramChatMessageId, String title) { // todo add results parser
        String body = jenkinsConfigGenerator.getConfig(opts, githubRepositoryUrl, telegramChatMessageId, title);
        String createJobUrl = format(API_CREATE_JOB_URL_TEMPLATE, jenkinsConfig.getUrl(), jiraIssueKey);
        String jobUrl = format(JOB_URL_TEMPLATE, jenkinsConfig.getUrl(), jiraIssueKey);

        HttpResponse<String> createJobResponse = Unirest
                .post(createJobUrl)
                .header("Content-Type", "text/xml; charset=utf-8")
                .basicAuth(jenkinsConfig.getUsername(), jenkinsConfig.getToken())
                .body(body).asString();

        log.error(createJobResponse.getBody());
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

        log.error(createJobResponse.getBody());
        return jobUrl; // todo add exception or move to github actions
    }

    public void awaitJobFinished(String jiraIssueKey) {
        Awaitility.await().atMost(120, SECONDS)
                .pollInterval(3, SECONDS)
                .until(() -> isJobFinished(jiraIssueKey));
    }

    public boolean isJobFinished(String jiraIssueKey) {
        String jobStatusUrl = format(API_JOB_STATUS_URL_TEMPLATE, jenkinsConfig.getUrl(), jiraIssueKey);
        log.info(jobStatusUrl);

        GetRequest jobGet = Unirest.get(jobStatusUrl);
        log.info(jobGet.toString());

        HttpResponse<JsonNode> jobBodyAsJson = jobGet.asJson();
        log.info(jobBodyAsJson.toString());

        JsonNode jobBody = jobBodyAsJson.getBody();
        log.info(jobBody.toString());

        JSONObject jobObject = jobBody.getObject();
        log.info(jobObject.toString());

        return !jobObject.isNull("result") && !jobObject.getBoolean("building");
    }
}
