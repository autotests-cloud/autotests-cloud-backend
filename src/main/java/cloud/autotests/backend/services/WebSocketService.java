package cloud.autotests.backend.services;

import cloud.autotests.backend.exceptions.ReCaptchaInvalidException;
import cloud.autotests.backend.models.GithubTestClass;
import cloud.autotests.backend.models.JiraIssue;
import cloud.autotests.backend.models.TelegramMessage;
import cloud.autotests.backend.models.request.GenerateRequest;
import cloud.autotests.backend.models.websocket.WebsocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import static cloud.autotests.backend.config.TelegramConfig.TELEGRAM_DISCUSSION_URL_TEMPLATE;
import static cloud.autotests.backend.utils.Utils.getAuthority;
import static java.lang.Thread.sleep;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {
    private final CaptchaService captchaService;
    private final JiraService jiraService;
    private final GithubService githubService;
    private final JenkinsService jenkinsService;
    private final TelegramService telegramService;
    private final SimpMessageSendingOperations messagingTemplate;

    @SneakyThrows
    public void sendMessage(String uniqueUserId, WebsocketMessage websocketMessage) {
        sleep(1000);
        messagingTemplate.convertAndSend("/topic/" + uniqueUserId, websocketMessage);
    }

    public void generate(String uniqueUserId, GenerateRequest request) throws InterruptedException {

        try {
            captchaService.processResponse(request.getCaptcha());
        } catch (ReCaptchaInvalidException e) {
            sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setPrefix("x")
                            .setContentType("error")
                            .setContent("Cant validate captcha " + e));
            return;
        }

        log.info("[START] Generate component {}", request.getCollectionName());
        String title = getAuthority(request.getOpts().getUrl());

        log.info("[START] Generate jira task {}", title);
        JiraIssue jiraIssue = jiraService.createTask(title + " tests");

        if (jiraIssue.getKey() == null) {
            sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setPrefix("x")
                            .setContentType("error")
                            .setContent("Cant create jira issue"));
            return;
        }
        log.info("[FINISH] Generate jira issue {}", jiraIssue.getKey());

//        sendMessage(uniqueUserId,
//                new WebsocketMessage()
//                        .setContentType("generated")
//                        .setContent("Jira issue created (authorization required): ")
//                        .setUrl(jiraIssueUrl)
//                        .setUrlText(jiraIssueKey));
//        sendMessage(uniqueUserId,
//                new WebsocketMessage()
//                        .setContentType("info")
//                        .setContent("Our engineers are already working on it"));
//        sendMessage(uniqueUserId,
//                new WebsocketMessage()
//                        .setPrefix(">")
//                        .setContentType("empty"));
        log.info("[START] Generate girhub repository {}", jiraIssue.getKey());
        String githubRepositoryUrl = githubService.createRepositoryFromTemplate(jiraIssue.getKey());
        if (githubRepositoryUrl == null) {
            sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setPrefix("x")
                            .setContentType("error")
                            .setContent("Cant create github repository"));
            return;
        }

        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("$")
                        .setContentType("generated")
                        .setContent("Github repository created: ")
                        .setUrl(githubRepositoryUrl)
                        .setUrlText(githubRepositoryUrl.replace("https://", "")));

        sleep(2000);
        log.info("[FINISH] Generate girhub repository {}", jiraIssue.getKey());

        log.info("[START] Generate tests with {}", jiraIssue.getKey());
        GithubTestClass githubTests = githubService.generateTests(request.getOpts().getTests(), jiraIssue.getKey(), title);
        if (githubTests == null) {
            sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setPrefix("x")
                            .setContentType("error")
                            .setContent("Cant create tests class in github"));
            return;
        }
        String githubTestsUrl = githubTests.getUrl();
        String githubTestsUrlText = githubTests.getUrlText();
        log.info("[FINISH] Generate tests with {}", jiraIssue.getKey());

        log.info("[START] Generate channel post");
        TelegramMessage.Info postInfo = telegramService.createChannelPost(request.getOpts(), jiraIssue, githubTests.getUrl());
        if (postInfo == null) {
            sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setPrefix("x")
                            .setContentType("error")
                            .setContent("Cant create telegram channel post"));
            return;
        }
        TelegramMessage.Info chatInfo = telegramService.awaitChatMessageObject(postInfo.getId());
        TelegramMessage telegramMessage = TelegramMessage.builder()
                .chat(chatInfo)
                .post(postInfo)
                .build();

        log.info("[FINISH] Generate channel post");

        log.info("[START] Generate jenkins job");
        String jenkinsJobUrl = jenkinsService.createJob(request.getOpts(), jiraIssue.getKey(),
                githubRepositoryUrl, telegramMessage.getChat().getId(), title);
        log.info("[FINISH] Generate jenkins job");

        log.info("[START] launch job");
        jenkinsService.launchJob(jiraIssue.getKey());
        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("$")
                        .setContentType("generated")
                        .setContent("Autotests code generated: ")
                        .setUrl(githubTestsUrl)
                        .setUrlText(githubTestsUrlText));
        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("info")
                        .setContent("Code stack: Java, Gradle, JUnit5, AssertJ, Owner, Rest-Assured, Selenide, Allure"));
        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("empty"));

        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("$")
                        .setContentType("generated")
                        .setContent("Jenkins job created: ")
                        .setUrl(jenkinsJobUrl)
                        .setUrlText(jenkinsJobUrl.replace("https://", "")));

        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("$")
                        .setContentType("launched")
                        .setContent("Jenkins job launched, autotests are running (~1 min): ")
                        .setUrl(jenkinsJobUrl + "/1/console")
                        .setUrlText("/1/console"));
        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("info")
                        .setContent("Infrastructure stack: Github, Jenkins, Docker, Selenoid"));

        jenkinsService.awaitJobFinished(jiraIssue.getKey());
        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("$")
                        .setContentType("finished")
                        .setContent("Jenkins job finished: ")
                        .setUrl(jenkinsJobUrl + "/1/allure")
                        .setUrlText("/1/allure"));
        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("info")
                        .setContent("Report available with test details, screenshots, logs, videos"));
        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("empty"));
        log.info("[FINISH] launch job");



//        telegramService.addOnBoardingMessage(telegramChatMessageId);
////        if (telegramChatMessageId == null) {
////            return new ResponseEntity<>("Cant add comment to telegram channel post", HttpStatus.INTERNAL_SERVER_ERROR);
////        }

        log.info("[START] update тask {}", jiraIssue.getKey());
        boolean jiraUpdateIssueResult = jiraService.updateTask(request.getOpts(), jiraIssue.getKey(), githubTests.getUrl(), telegramMessage.getPost().getId());
        if (!jiraUpdateIssueResult) {
            sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setPrefix("x")
                            .setContentType("error")
                            .setContent("Cant update jira issue"));
        }
        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("telegram-notification")
                        .setContent(telegramMessage.getChat().getName() + "/" + telegramMessage.getChat().getId()));
        log.info("[FINISH] update тask {}", jiraIssue.getKey());



        String telegramDiscussionUrl = String.format(TELEGRAM_DISCUSSION_URL_TEMPLATE,
                telegramMessage.getPost().getName(),  telegramMessage.getPost().getId(), telegramMessage.getChat().getId());

        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("info")
                        .setContent("<span class=\"violet-text\">What's next?</span>"));
        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("1")
                        .setContentType("info")
                        .setContent("Congratulations! Now you have a ready test automation project: <span class=\"yellow-text\">// todo FAQ</span>")); // todo FAQ
        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("2")
                        .setContentType("can-automate")
                        .setContent("<a target=\"_blank\" class=\"green-link\" href=\"https://qa.guru\">QA.GURU</a> engineers can automate your tests (<b>Web</b>, <b>Android</b>, <b>iOS</b>, <b>API</b>)."));
        sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(" ")
                        .setContentType("telegram-info")
                        .setContent("Discuss details and payment: ")
                        .setUrl(telegramDiscussionUrl)
                        .setUrlText(telegramDiscussionUrl.replace("https://", "")));

        log.info("[FINISH] Generate component {}", request.getCollectionName());
    }
}
