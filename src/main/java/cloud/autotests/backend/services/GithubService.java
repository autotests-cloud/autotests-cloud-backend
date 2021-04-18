package cloud.autotests.backend.services;

import cloud.autotests.backend.builders.TestBuilder;
import cloud.autotests.backend.config.GithubConfig;
import cloud.autotests.backend.models.Order;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Base64;

public class GithubService {
    private static final Logger LOG = LoggerFactory.getLogger(GithubService.class);

    private final String TEMPLATE_REPOSITORY_URL = "https://api.github.com/repos/%s/%s/generate";
    private final String NEW_TEST_REPOSITORY_PATH = "https://api.github.com/repos/%s/%s/contents/" +
            "src/test/java/cloud/autotests/tests/AppTests.java";

    private final String githubToken;
    private final String githubTemplateRepositoryApiUrl;
    private final String githubGeneratedOwner;

    @Autowired
    public GithubService(GithubConfig githubConfig) {
        this.githubToken = githubConfig.getGithubToken();
        this.githubTemplateRepositoryApiUrl = String.format(TEMPLATE_REPOSITORY_URL,
                githubConfig.getGithubTemplateOwner(), githubConfig.getGithubTemplateRepository());
        this.githubGeneratedOwner = githubConfig.getGithubGeneratedOwner();
    }

    public String createRepositoryFromTemplate(String jiraIssueKey) {
        String bodyTemplate = "{\"owner\": \"%s\", \"name\": \"%s\"}";
        String body = String.format(bodyTemplate, this.githubGeneratedOwner, jiraIssueKey);

        JSONObject createRepositoryResponse = Unirest
                .post(this.githubTemplateRepositoryApiUrl)
                .header("Accept", "application/vnd.github.baptiste-preview+json")
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "token " + this.githubToken)
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

        if (createRepositoryResponse.has("html_url"))
            return createRepositoryResponse.getString("html_url");

        return null;

        // todo add exception for existing repo
        /*
        {
          "message": "Unprocessable Entity",
          "errors": [
            "Could not clone: Name already exists on this account"
          ],
          "documentation_url": "https://docs.github.com/rest/reference/repos#create-a-repository-using-a-template"
        }
         */
    }

    public String generateTests(Order order, String jiraIssueKey) {
        String testClassPath = String.format(NEW_TEST_REPOSITORY_PATH, this.githubGeneratedOwner, jiraIssueKey);
        String testClassContent = new TestBuilder().generateTestClass(order); // todo rude
        String testClassContent64 = Base64.getEncoder().encodeToString(testClassContent.getBytes());

        String bodyTemplate = "{\"message\": \"Added test '%s'\", \"content\": \"%s\"}";
        String body = String.format(bodyTemplate, order.getTitle(), testClassContent64);

        JSONObject createTestsResponse = Unirest
                .put(testClassPath)
                .header("Accept", "application/vnd.github.v3+json")
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "token " + this.githubToken)
                .body(body)
                .asJson()
                .ifFailure(response -> {
                    LOG.error("[createTestsResponse] Oh No! Status" + response.getStatus());
                    LOG.error(response.getStatusText());
                    LOG.error(response.getBody().toPrettyString());
                    response.getParsingError().ifPresent(e -> {
                        LOG.error("Parsing Exception: ", e);
                        LOG.error("Original body: " + e.getOriginalBody());
                    });
                })
                .getBody()
                .getObject();

        if (createTestsResponse.has("message"))
            if (createTestsResponse.getString("message").contains("Invalid request.\\n\\n\\\"sha\\\" wasn't supplied."))
                return null; // todo add normal exception

        if (createTestsResponse.has("content")) {
            JSONObject contentJson = createTestsResponse.getJSONObject("content");
            if (contentJson.has("html_url")) {
                return contentJson.getString("html_url");
            } else {
                return null; // todo add exception
            }
        }
        return null;
    }

}
