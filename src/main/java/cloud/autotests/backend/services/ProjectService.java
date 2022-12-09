package cloud.autotests.backend.services;

import cloud.autotests.backend.exceptions.BadRequestException;
import cloud.autotests.backend.models.GithubTestClass;
import cloud.autotests.backend.models.JiraIssue;
import cloud.autotests.backend.models.TelegramMessage;
import cloud.autotests.backend.models.request.GenerateRequest;
import cloud.autotests.backend.models.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

import static cloud.autotests.backend.config.TelegramConfig.TELEGRAM_DISCUSSION_URL_TEMPLATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final CaptchaService captchaService;
    private final JiraService jiraService;
    private final GithubService githubService;
    private final JenkinsService jenkinsService;
    private final TelegramService telegramService;

    public ResponseEntity<?> generate(GenerateRequest request) {

        captchaService.processResponse(request.getCaptcha());

        log.info("[START] Generate component {}", request.getCollectionName());
        String title = getAuthority(request.getOpts().getUrl());

        log.info("[START] Generate jira task {}", title);
        JiraIssue jiraIssue = jiraService.createTask(title + " tests");
        log.info("[FINISH] Generate jira issue {}", jiraIssue.getKey());

        log.info("[START] Generate girhub repository {}", jiraIssue.getKey());
        String githubRepositoryUrl = githubService.createRepositoryFromTemplate(jiraIssue.getKey());
        log.info("[FINISH] Generate girhub repository {}", jiraIssue.getKey());

        log.info("[START] Generate tests with {}", jiraIssue.getKey());
        GithubTestClass githubTests = githubService.generateTests(request.getOpts().getTests(), jiraIssue.getKey(), title);
        log.info("[FINISH] Generate tests with {}", jiraIssue.getKey());

        log.info("[START] Generate channel post");
        TelegramMessage telegramMessage = telegramService.createChannelPostAndAwaitChatMessageObject(request.getOpts(), jiraIssue, githubTests.getUrl());
        log.info("[FINISH] Generate channel post");

        String jenkinsJobUrl = jenkinsService.createJob(request.getOpts(), jiraIssue.getKey(),
                githubRepositoryUrl, telegramMessage.getChat().getId(), title);

        log.info("[START] launch job");
        jenkinsService.launchJob(jiraIssue.getKey());
        jenkinsService.awaitJobFinished(jiraIssue.getKey());
        log.info("[FINISH] launch job");

        log.info("[START] update тask {}", jiraIssue.getKey());
        jiraService.updateTask(request.getOpts(), jiraIssue.getKey(), githubTests.getUrl(), telegramMessage.getPost().getId());
        log.info("[FINISH] update тask {}", jiraIssue.getKey());

        String telegramDiscussionUrl = String.format(TELEGRAM_DISCUSSION_URL_TEMPLATE,
                telegramMessage.getPost().getName(),  telegramMessage.getPost().getId(), telegramMessage.getChat().getId());

        log.info("[FINISH] Generate component {}", request.getCollectionName());

        return ResponseEntity.badRequest().body(ResultResponse.builder()
                        .telegramDiscussionUrl(telegramDiscussionUrl)
                        .jenkinsJobUrl(jenkinsJobUrl)
                        .jiraIssue(jiraIssue)
                .build());
    }

    public String getAuthority(String url) {

        log.info("getAuthority with {}", url);
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            log.error("getAuthority " + url, e);
            throw new BadRequestException(e.getMessage());
        }
        String authority = uri.getAuthority();
        log.info("getAuthority result {}", authority);

        return authority;
    }
}
