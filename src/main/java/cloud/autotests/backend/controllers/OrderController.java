package cloud.autotests.backend.controllers;

import cloud.autotests.backend.models.*;
import cloud.autotests.backend.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import static cloud.autotests.backend.config.TelegramConfig.TELEGRAM_DISCUSSION_URL_TEMPLATE;
import static cloud.autotests.backend.utils.CleanContentUtils.cleanOrder;
import static java.lang.String.format;
import static java.lang.Thread.sleep;

@Controller
public class OrderController {
    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    WebSocketService webSocketService;
    @Autowired
    JiraService jiraService;
    @Autowired
    GithubService githubService;
    @Autowired
    JenkinsService jenkinsService;
    @Autowired
    TelegramService telegramService;

    @MessageMapping("/orders/{uniqueUserId}")
    public void createOrder(@DestinationVariable("uniqueUserId") String uniqueUserId, @RequestBody Order rawOrder) throws InterruptedException {
        Order order = cleanOrder(rawOrder);

        JiraIssue jiraIssue = jiraService.createTask(order); // todo move
        String jiraIssueKey = jiraIssue.getKey();
        String jiraIssueUrl = jiraIssue.getUrl();

        if (jiraIssueKey == null) {
            webSocketService.sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setContentType("error")
                            .setContent("Cant create jira issue"));
            return;
        }

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("generated")
                        .setContent("Jira issue created (authorization required): ")
                        .setUrl(jiraIssueUrl)
                        .setUrlText(jiraIssueKey));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("info")
                        .setContent("Our engineers are already working on it"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("empty"));

        String githubRepositoryUrl = githubService.createRepositoryFromTemplate(jiraIssueKey);
        if (githubRepositoryUrl == null) {
            webSocketService.sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setContentType("error")
                            .setContent("Cant create github repository"));
            return;
        }

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("generated")
                        .setContent("Github repository created: ")
                        .setUrl(githubRepositoryUrl)
                        .setUrlText(githubRepositoryUrl.replace("https://", "")));

        sleep(2000);
        GithubTestClass githubTests = githubService.generateTests(order, jiraIssueKey);
        if (githubTests == null) {
            webSocketService.sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setContentType("error")
                            .setContent("Cant create tests class in github"));
            return;
        }
        String githubTestsUrl = githubTests.getUrl();
        String githubTestsUrlText = githubTests.getUrlText();

        TelegramMessage telegramChannelPostObject = telegramService.createChannelPost(order, jiraIssue, githubTestsUrl);
        if (telegramChannelPostObject == null) {
            webSocketService.sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setContentType("error")
                            .setContent("Cant create telegram channel post"));
            return;
        }
        Integer telegramChannelPostId = telegramChannelPostObject.getId();
        String telegramChannelName = telegramChannelPostObject.getName();

        TelegramMessage telegramChatMessageObject = telegramService.awaitChatMessageObject(telegramChannelPostId);
        // todo add exception
        String telegramChatName = telegramChatMessageObject.getName();
        Integer telegramChatMessageId = telegramChatMessageObject.getId();

        String jenkinsJobUrl = jenkinsService.createJob(order, jiraIssueKey,
                githubRepositoryUrl, telegramChatMessageId);

        jenkinsService.launchJob(jiraIssueKey);

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("generated")
                        .setContent("Autotests code generated: ")
                        .setUrl(githubTestsUrl)
                        .setUrlText(githubTestsUrlText));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("info")
                        .setContent("Code stack: Java, Gradle, JUnit5, AssertJ, Owner, Rest-Assured, Selenium, Selenide, Allure"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("empty"));

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("generated")
                        .setContent("Jenkins job created: ")
                        .setUrl(jenkinsJobUrl)
                        .setUrlText(jenkinsJobUrl.replace("https://", "")));

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("generated")
                        .setContent("Jenkins job launched, autotests are running (~1 min): ")
                        .setUrl(jenkinsJobUrl + "/1/console")
                        .setUrlText("/1/console"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("info")
                        .setContent("Infrastructure stack: Github, Jenkins, Docker, Selenoid"));

//        telegramService.addOnBoardingMessage(telegramChatMessageId);
////        if (telegramChatMessageId == null) {
////            return new ResponseEntity<>("Cant add comment to telegram channel post", HttpStatus.INTERNAL_SERVER_ERROR);
////        }

        jenkinsService.awaitJobFinished(jiraIssueKey);

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("generated")
                        .setContent("Jenkins job finished: ")
                        .setUrl(jenkinsJobUrl + "/1/allure")
                        .setUrlText("/1/allure"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("info")
                        .setContent("Report available with test details, screenshots, logs, videos"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("empty"));

        Boolean jiraUpdateIssueResult = jiraService.updateTask(order, jiraIssueKey, githubTestsUrl, telegramChannelPostId);
        if (jiraUpdateIssueResult == null) {
            webSocketService.sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setContentType("error")
                            .setContent("Cant update jira issue"));
        }

        String telegramDiscussionUrl = format(TELEGRAM_DISCUSSION_URL_TEMPLATE,
                telegramChannelName, telegramChannelPostId, telegramChatMessageId);
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("telegram-info")
                        .setContent("Telegram chat started: ")
                        .setUrl(telegramDiscussionUrl)
                        .setUrlText(telegramDiscussionUrl.replace("https://", "")));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("info")
                        .setContent("Join to discuss and specify your task!"));

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("telegram-notification")
                        .setContent(telegramChatName + "/" + telegramChatMessageId));
    }

}
