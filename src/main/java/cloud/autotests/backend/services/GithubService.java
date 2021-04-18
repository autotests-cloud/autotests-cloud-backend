package cloud.autotests.backend.services;

import cloud.autotests.backend.config.GithubConfig;
import cloud.autotests.backend.config.TelegramConfig;
import cloud.autotests.backend.models.Order;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class GithubService {
    private static final Logger LOG = LoggerFactory.getLogger(GithubService.class);

    private String githubToken;
    private String githubTemplateRepositoryApiUrl;
    private String githubTemplateGenerateOwner;

    @Autowired
    public GithubService(GithubConfig githubConfig) {
        this.githubToken = githubConfig.getGithubToken();
        this.githubTemplateRepositoryApiUrl = String.format("https://api.github.com/repos/%s/generate",
                githubConfig.getGithubTemplateRepository());
        this.githubTemplateGenerateOwner = githubConfig.getGithubTemplateGenerateOwner();
    }

    public String createRepositoryFromTemplate(String jiraIssueKey) {
        String body = String.format("{\"owner\": \"%s\", \"name\": \"%s\"}", this.githubTemplateGenerateOwner, jiraIssueKey);

        JSONObject createRepositoryResponse = Unirest
                .post(this.githubTemplateRepositoryApiUrl)
                .header("Accept", "application/vnd.github.baptiste-preview+json")
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "token " + this.githubToken)
                .body(body)
//                .field("owner", this.githubTemplateGenerateOwner)
//                .field("name", jiraIssueKey)
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

        if (createRepositoryResponse.has("html_url"))
            return createRepositoryResponse.getString("html_url");
        return null;
    }


}
