package cloud.autotests.backend.services;

import cloud.autotests.backend.builders.TestBuilder;
import cloud.autotests.backend.config.GithubConfig;
import cloud.autotests.backend.config.JenkinsConfig;
import cloud.autotests.backend.models.Order;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;

public class JenkinsService {
    private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);

    private final String CREATE_JOB_URL = "https://jenkins.autotests.cloud/createItem?name=%s";

    private final String jenkinsUrl;
    private final String jenkinsUsername;
    private final String jenkinsToken;
    private final String jenkinsTemplateJob;

    @Autowired
    public JenkinsService(JenkinsConfig jenkinsConfig) {
        this.jenkinsUrl = jenkinsConfig.getJenkinsUrl();
        this.jenkinsUsername = jenkinsConfig.getJenkinsUsername();
        this.jenkinsToken = jenkinsConfig.getJenkinsToken();
        this.jenkinsTemplateJob = jenkinsConfig.getJenkinsTemplateJob();
    }
    // curl -s -XPOST 'https://jenkins.autotests.cloud/createItem?name=TM-534' -u svasenkov:11b89cec73645ede1174fdf65c22bfaf9d --data-binary @config.xml -H "Content-Type:text/xml"
    private Integer createJob(String body, String jiraIssueKey) {
        JSONObject createMessageResponse = Unirest
                .post(String.format(CREATE_JOB_URL, jiraIssueKey))
                .header("Content-Type", "text/xml; charset=utf-8")
                .basicAuth(this.jenkinsUsername, this.jenkinsToken)
                .body(body)
                .asJson()
                .ifFailure(response -> {
                    LOG.error("Oh No! Status" + response.getStatus());
                    LOG.error(response.getStatusText());
                    LOG.error(response.getBody().toPrettyString());
                    response.getParsingError().ifPresent(e -> {
                        LOG.error("Parsing Exception: ", e);
                        LOG.error("Original body: " + e.getOriginalBody());
                    });
                })
                .getBody()
                .getObject();

        boolean messageResponse = createMessageResponse.getBoolean("ok");
        if (messageResponse)
            return createMessageResponse.getJSONObject("result")
                    .getInt("message_id");
        return null;
    }
}
