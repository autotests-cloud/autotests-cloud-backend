package cloud.autotests.backend.services;

import cloud.autotests.backend.config.GithubConfig;
import cloud.autotests.backend.exceptions.CreateObjectException;
import cloud.autotests.backend.exceptions.ServerException;
import cloud.autotests.backend.models.GithubTestClass;
import cloud.autotests.backend.models.request.Tests;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;

import static cloud.autotests.backend.config.GithubConfig.*;
import static cloud.autotests.backend.generators.tests.OnBoardingTestClassGenerator.generateOnBoardingTestClass;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubService {
    private final String CLASS_NAME_PREFIX = "Generated";
    private final GithubConfig githubConfig;

    public String createRepositoryFromTemplate(String jiraIssueKey) {

        JSONObject createRepositoryResponse =  createRepository(jiraIssueKey);

        String githubRepositoryUrl = null;
        if (createRepositoryResponse.has("html_url"))
            githubRepositoryUrl =  createRepositoryResponse.getString("html_url");

        if (githubRepositoryUrl == null) {
            log.error("[ERROR] Generate github repositorye title {}" + jiraIssueKey);
            throw new CreateObjectException("github repository");
        }

        return githubRepositoryUrl;
    }

    private JSONObject createRepository(String jiraIssueKey) {
        String bodyTemplate = "{\"owner\": \"%s\", \"name\": \"%s\"}";
        String body = format(bodyTemplate, githubConfig.getGithubGeneratedOwner(), jiraIssueKey);
        String githubTemplateRepositoryApiUrl = format(API_TEMPLATE_REPOSITORY_URL,
                githubConfig.getGithubTemplateOwner(), githubConfig.getGithubTemplateRepository());

        return Unirest
                .post(githubTemplateRepositoryApiUrl)
                .header("Accept", "application/vnd.github.baptiste-preview+json")
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "token " + githubConfig.getGithubToken())
                .body(body)
                .asJson()
                .ifFailure(response -> {
                    log.error("Oh No! Status" + response.getStatus());
                    log.error(response.getStatusText());
                    log.error(response.getBody().toPrettyString());
                    response.getParsingError().ifPresent(e -> {
                        log.error("Parsing Exception: ", e);
                        log.error("Original body: " + e.getOriginalBody());
                    });
                })
                .getBody()
                .getObject();
    }

    public GithubTestClass generateTests(Tests tests, String jiraIssueKey, String title) {

        String generatedTestsContent = generateOnBoardingTestClass(tests, CLASS_NAME_PREFIX, title);
        String testClassContent64 = Base64.getEncoder().encodeToString(generatedTestsContent.getBytes());

        String testClassPath = format(API_NEW_TEST_CLASS_PATH,
                githubConfig.getGithubGeneratedOwner(), jiraIssueKey, CLASS_NAME_PREFIX);

        String bodyTemplate = "{\"message\": \"Added test '%s'\", \"content\": \"%s\"}";
        String body = format(bodyTemplate, title, testClassContent64);

        JSONObject createTestsResponse = Unirest
                .put(testClassPath)
                .header("Accept", "application/vnd.github.v3+json")
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "token " + githubConfig.getGithubToken())
                .body(body)
                .asJson()
                .ifFailure(response -> {
                    log.error("[createTestsResponse] Oh No! Status" + response.getStatus());
                    log.error(response.getStatusText());
                    log.error(response.getBody().toPrettyString());
                    response.getParsingError().ifPresent(e -> {
                        log.error("Parsing Exception: ", e);
                        log.error("Original body: " + e.getOriginalBody());
                    });
                })
                .getBody()
                .getObject();

        if (createTestsResponse.has("message") &&
                createTestsResponse.getString("message").contains("Invalid request.\\n\\n\\\"sha\\\" wasn't supplied.")) {
            throw new CreateObjectException( createTestsResponse.getString("message"));
        }

        GithubTestClass githubTestClass = null;
        if (createTestsResponse.has("content")) {
            JSONObject contentJson = createTestsResponse.getJSONObject("content");
            if (contentJson.has("html_url")) {
                githubTestClass = new GithubTestClass()
                        .setUrl(contentJson.getString("html_url"))
                        .setUrlText(format(NEW_TEST_CLASS_SHORTENED_URL, CLASS_NAME_PREFIX));
            } else {
                throw new ServerException("Test class generate error");
            }
        }

        return githubTestClass;
    }
}
