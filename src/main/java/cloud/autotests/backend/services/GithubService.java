package cloud.autotests.backend.services;

import cloud.autotests.backend.config.GithubConfig;
import cloud.autotests.backend.models.GithubTestClass;
import cloud.autotests.backend.models.Order;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Base64;

import static cloud.autotests.backend.config.GithubConfig.*;
import static cloud.autotests.backend.generators.tests.OnBoardingTestClassGenerator.generateOnBoardingTestClass;
import static java.lang.String.format;

@Service
@AllArgsConstructor
public class GithubService {
    private static final Logger LOG = LoggerFactory.getLogger(GithubService.class);

    private final GithubConfig githubConfig;

    public String createRepositoryFromTemplate(String jiraIssueKey) {
        String bodyTemplate = "{\"owner\": \"%s\", \"name\": \"%s\"}";
        String body = format(bodyTemplate, githubConfig.getGithubGeneratedOwner(), jiraIssueKey);
        String githubTemplateRepositoryApiUrl = format(API_TEMPLATE_REPOSITORY_URL,
                githubConfig.getGithubTemplateOwner(), githubConfig.getGithubTemplateRepository());

        JSONObject createRepositoryResponse = Unirest
                .post(githubTemplateRepositoryApiUrl)
                .header("Accept", "application/vnd.github.baptiste-preview+json")
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "token " + githubConfig.getGithubToken())
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

    public GithubTestClass generateTests(Order order, String jiraIssueKey) {
        final String testClassNamePrefix = "Generated";
        String generatedTestsContent = generateOnBoardingTestClass(testClassNamePrefix, order);
        String testClassContent64 = Base64.getEncoder().encodeToString(generatedTestsContent.getBytes());

        String testClassPath = format(API_NEW_TEST_CLASS_PATH,
                githubConfig.getGithubGeneratedOwner(), jiraIssueKey, testClassNamePrefix);

        String bodyTemplate = "{\"message\": \"Added test '%s'\", \"content\": \"%s\"}";
        String body = format(bodyTemplate, order.getTitle(), testClassContent64);

        JSONObject createTestsResponse = Unirest
                .put(testClassPath)
                .header("Accept", "application/vnd.github.v3+json")
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "token " + githubConfig.getGithubToken())
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
                return new GithubTestClass()
                        .setUrl(contentJson.getString("html_url"))
                        .setUrlText(format(NEW_TEST_CLASS_SHORTENED_URL, testClassNamePrefix));
            } else {
                return null; // todo add exception
            }
        }
        return null;
    }

}
