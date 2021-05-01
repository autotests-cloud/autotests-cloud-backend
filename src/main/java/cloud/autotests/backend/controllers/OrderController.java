package cloud.autotests.backend.controllers;

import cloud.autotests.backend.models.JiraIssue;
import cloud.autotests.backend.models.Order;
import cloud.autotests.backend.models.TelegramMessage;
import cloud.autotests.backend.models.WebsocketMessage;
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
                        .setContent("Github repository created"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("green-link")
                        .setUrl(githubRepositoryUrl));

        sleep(2000);
        String githubTestsUrl = githubService.generateTests(order, jiraIssueKey);
        if (githubTestsUrl == null) {
            webSocketService.sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setContentType("error")
                            .setContent("Cant create tests class in github"));
            return;
        }

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
        String telegramChatName = telegramChatMessageObject.getName();
        Integer telegramChatMessageId = telegramChatMessageObject.getId();

        String jenkinsJobUrl = jenkinsService.createJob(order, jiraIssueKey,
                githubRepositoryUrl, telegramChatMessageId);

        jenkinsService.launchJob(jiraIssueKey);

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("generated")
                        .setContent("Autotests code generated"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("green-link")
                        .setUrl(githubTestsUrl));
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
                        .setContent("Jenkins job created"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("green-link")
                        .setUrl(jenkinsJobUrl));

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("generated")
                        .setContent("Jenkins job launched, autotests are running..."));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("green-link")
                        .setUrl(jenkinsJobUrl + "/1/console"));
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
                        .setContent("Jenkins job finished. Report available with test details, screenshots, logs, videos"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("green-link")
                        .setUrl(jenkinsJobUrl + "/1/allure"));

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("telegram-notification")
                        .setContent(telegramChatName + "/" + telegramChatMessageId));

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("generated")
                        .setContent("Jira issue created (available for qa.guru engineers only)"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("green-link")
                        .setUrl(jiraIssueUrl));
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
                        .setContentType("info")
                        .setContent("Telegram chat started. Join to specify and discuss your task!"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setContentType("blue-link")
                        .setUrl(telegramDiscussionUrl));

    }

}
