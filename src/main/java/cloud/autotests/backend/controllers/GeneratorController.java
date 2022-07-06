package cloud.autotests.backend.controllers;

import cloud.autotests.backend.config.DebugConfig;
import cloud.autotests.backend.exceptions.ReCaptchaInvalidException;
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
public class GeneratorController {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorController.class);

    @Autowired
    WebSocketService webSocketService;
    @Autowired
    CaptchaService captchaService;
    @Autowired
    JiraService jiraService;
    @Autowired
    GithubService githubService;
    @Autowired
    JenkinsService jenkinsService;
    @Autowired
    TelegramService telegramService;

    @Autowired
    private DebugConfig debugConfig;

    @MessageMapping("/generate/{uniqueUserId}")
//    public void createOrder(@DestinationVariable("uniqueUserId") String uniqueUserId, @RequestBody Order rawOrder) throws InterruptedException {
    public void createOrder(@DestinationVariable("uniqueUserId") String uniqueUserId, @RequestBody GeneratorModel generator) throws InterruptedException {
//    public void createOrder(@DestinationVariable("uniqueUserId") String uniqueUserId, @RequestBody String json) throws InterruptedException {

//        if(debugConfig.getDebugMode()) {
//            LOG.info(json);
//
//            webSocketService.sendMessage(uniqueUserId,
//                    new WebsocketMessage()
//                            .setContentType("info")
//                            .setContent("requested body: " + json));
//            sleep(3000);
//
//            webSocketService.sendMessage(uniqueUserId,
//                    new WebsocketMessage()
//                            .setContentType("git")
//                            .setContent("https://github.com/autotests-cloud/AUTO-1150"));
//            sleep(3000);
//
//            webSocketService.sendMessage(uniqueUserId,
//                    new WebsocketMessage()
//                            .setContentType("jenkins")
//                            .setContent("https://jenkins.autotests.cloud/job/AUTO-1150"));
//            sleep(3000);
//
//            webSocketService.sendMessage(uniqueUserId,
//                    new WebsocketMessage()
//                            .setContentType("jekins_log")
//                            .setContent("https://jenkins.autotests.cloud/job/AUTO-1150/2/logText/progressiveText?start=0"));
//            sleep(3000);
//
//            webSocketService.sendMessage(uniqueUserId,
//                    new WebsocketMessage()
//                            .setContentType("notification")
//                            .setContent("autotests_cloud_chat/3707"));
//
//            webSocketService.sendMessage(uniqueUserId,
//                    new WebsocketMessage()
//                            .setContentType("text")
//                            .setContent("any text here"));
//            sleep(3000);
//            return;
//        }
        Order rawOrder = new Order(); // todo remove
        Order order = cleanOrder(rawOrder);
        String captcha = order.getCaptcha();

        try {
            captchaService.processResponse(captcha);
        } catch (ReCaptchaInvalidException e) {
            webSocketService.sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setPrefix("x")
                            .setContentType("error")
                            .setContent("Cant validate captcha " + e));
            return;
        }


        JiraIssue jiraIssue = jiraService.createTask(order); // todo move
        String jiraIssueKey = jiraIssue.getKey();
        String jiraIssueUrl = jiraIssue.getUrl();

        if (jiraIssueKey == null) {
            webSocketService.sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setPrefix("x")
                            .setContentType("error")
                            .setContent("Cant create jira issue"));
            return;
        }

//        webSocketService.sendMessage(uniqueUserId,
//                new WebsocketMessage()
//                        .setContentType("generated")
//                        .setContent("Jira issue created (authorization required): ")
//                        .setUrl(jiraIssueUrl)
//                        .setUrlText(jiraIssueKey));
//        webSocketService.sendMessage(uniqueUserId,
//                new WebsocketMessage()
//                        .setContentType("info")
//                        .setContent("Our engineers are already working on it"));
//        webSocketService.sendMessage(uniqueUserId,
//                new WebsocketMessage()
//                        .setPrefix(">")
//                        .setContentType("empty"));

        String githubRepositoryUrl = githubService.createRepositoryFromTemplate(jiraIssueKey);
        if (githubRepositoryUrl == null) {
            webSocketService.sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setPrefix("x")
                            .setContentType("error")
                            .setContent("Cant create github repository"));
            return;
        }

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("$")
                        .setContentType("generated")
                        .setContent("Github repository created: ")
                        .setUrl(githubRepositoryUrl)
                        .setUrlText(githubRepositoryUrl.replace("https://", "")));

        sleep(2000);
        GithubTestClass githubTests = githubService.generateTests(order, jiraIssueKey);
        if (githubTests == null) {
            webSocketService.sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setPrefix("x")
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
                            .setPrefix("x")
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
                        .setPrefix("$")
                        .setContentType("generated")
                        .setContent("Autotests code generated: ")
                        .setUrl(githubTestsUrl)
                        .setUrlText(githubTestsUrlText));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("info")
                        .setContent("Code stack: Java, Gradle, JUnit5, AssertJ, Owner, Rest-Assured, Selenide, Allure"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("empty"));

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("$")
                        .setContentType("generated")
                        .setContent("Jenkins job created: ")
                        .setUrl(jenkinsJobUrl)
                        .setUrlText(jenkinsJobUrl.replace("https://", "")));

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("$")
                        .setContentType("launched")
                        .setContent("Jenkins job launched, autotests are running (~1 min): ")
                        .setUrl(jenkinsJobUrl + "/1/console")
                        .setUrlText("/1/console"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("info")
                        .setContent("Infrastructure stack: Github, Jenkins, Docker, Selenoid"));

//        telegramService.addOnBoardingMessage(telegramChatMessageId);
////        if (telegramChatMessageId == null) {
////            return new ResponseEntity<>("Cant add comment to telegram channel post", HttpStatus.INTERNAL_SERVER_ERROR);
////        }

        jenkinsService.awaitJobFinished(jiraIssueKey);

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("$")
                        .setContentType("finished")
                        .setContent("Jenkins job finished: ")
                        .setUrl(jenkinsJobUrl + "/1/allure")
                        .setUrlText("/1/allure"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("info")
                        .setContent("Report available with test details, screenshots, logs, videos"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("empty"));

        Boolean jiraUpdateIssueResult = jiraService.updateTask(order, jiraIssueKey, githubTestsUrl, telegramChannelPostId);
        if (jiraUpdateIssueResult == null) {
            webSocketService.sendMessage(uniqueUserId,
                    new WebsocketMessage()
                            .setPrefix("x")
                            .setContentType("error")
                            .setContent("Cant update jira issue"));
        }

        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("telegram-notification")
                        .setContent(telegramChatName + "/" + telegramChatMessageId));

        String telegramDiscussionUrl = format(TELEGRAM_DISCUSSION_URL_TEMPLATE,
                telegramChannelName, telegramChannelPostId, telegramChatMessageId);
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(">")
                        .setContentType("info")
                        .setContent("<span class=\"violet-text\">What's next?</span>"));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("1")
                        .setContentType("info")
                        .setContent("Congratulations! Now you have a ready test automation project: <span class=\"yellow-text\">// todo FAQ</span>")); // todo FAQ
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("2")
                        .setContentType("can-automate")
                        .setContent("<a target=\"_blank\" class=\"green-link\" href=\"https://qa.guru\">QA.GURU</a> engineers can automate your tests (<b>Web</b>, <b>Android</b>, <b>iOS</b>, <b>API</b>)."));
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix(" ")
                        .setContentType("telegram-info")
                        .setContent("Discuss details and payment: ")
                        .setUrl(telegramDiscussionUrl)
                        .setUrlText(telegramDiscussionUrl.replace("https://", "")));

    }

}
