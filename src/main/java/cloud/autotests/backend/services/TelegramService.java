package cloud.autotests.backend.services;

import cloud.autotests.backend.config.JenkinsConfig;
import cloud.autotests.backend.config.TelegramConfig;
import cloud.autotests.backend.exceptions.ServerException;
import cloud.autotests.backend.models.JiraIssue;
import cloud.autotests.backend.models.TelegramMessage;
import cloud.autotests.backend.models.request.Opts;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static cloud.autotests.backend.config.JenkinsConfig.JOB_URL_TEMPLATE;
import static cloud.autotests.backend.config.TelegramConfig.GET_UPDATES_URL;
import static cloud.autotests.backend.config.TelegramConfig.SEND_MESSAGE_URL;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.awaitility.Awaitility.await;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramService {

    private final TelegramConfig telegramConfig;
    private final JenkinsConfig jenkinsConfig;

    public TelegramMessage createChannelPostAndAwaitChatMessageObject(@NotNull Opts opts, JiraIssue jiraIssue, String githubTestUrl){
        TelegramMessage.Info postInfo = createChannelPost(opts, jiraIssue, githubTestUrl);
        TelegramMessage.Info chatInfo = awaitChatMessageObject(postInfo.getId());

        return TelegramMessage.builder()
                .chat(chatInfo)
                .post(postInfo)
                .build();
    }

    public TelegramMessage.Info createChannelPost(@NotNull Opts opts, JiraIssue jiraIssue, String githubTestUrl) {
        String jiraIssueKey = jiraIssue.getKey();
        String jenkinsJobUrl = format(JOB_URL_TEMPLATE, jenkinsConfig.getUrl(), jiraIssueKey);

        String message = format(
                "<u><b>Test title</b></u>: <code>%s</code>\n" +
                        "<u><b>Price</b></u>: [%s]\n" +
                        "<u><b>Jira issue</b></u>: <a href=\"%s\">%s</a>\n" +
                        "<u><b>Jenkins job</b></u>: <a href=\"%s\">%s</a>\n" +
                        "<u><b>Github code</b></u>:\n" +
                        "%s",
                opts.getUrl(), opts.getPrice(), jiraIssue.getUrl(), jiraIssueKey,
                jenkinsJobUrl, jiraIssueKey, githubTestUrl); // todo email

        String body = format("chat_id=%s&text=%s&parse_mode=html", telegramConfig.getChannelId(), message);

        JSONObject sendObject = sendText(body);

        return TelegramMessage.Info.builder()
                .id(sendObject.getInt("message_id"))
                .name(sendObject.getJSONObject("chat").getString("username"))
                .build();
    }

    public JSONObject addOnBoardingMessage(Integer chatMessageId) {
        String message = "Report will be here <u>in 1 minute</u>!";

        return sendText(format("chat_id=%s&reply_to_message_id=%s&text=%s&parse_mode=html",
                telegramConfig.getChatId(), chatMessageId, message));
    }

    private JSONObject sendText(String body) {
        String sendMessageUrl = format(SEND_MESSAGE_URL, telegramConfig.getToken());
        JSONObject createMessageResponse = Unirest.post(sendMessageUrl)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
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

        boolean messageResponse = createMessageResponse.getBoolean("ok");
        if (messageResponse)
            return createMessageResponse.getJSONObject("result");
        return null;
    }

    public TelegramMessage.Info awaitChatMessageObject(Integer channelPostId) {
        TelegramMessage.Info[] messages = new TelegramMessage.Info[1];
        await()
                .atMost(60, SECONDS)
                .pollInterval(5, SECONDS)
                .until(() -> {
                    TelegramMessage.Info msg = getChatMessageObject(channelPostId);
                    messages[0] = msg;
                    return msg != null;
                });
        return messages[0];
    }

    public TelegramMessage.Info getChatMessageObject(Integer channelPostId) {
        JSONArray getUpdatesList = getUpdates();

        List<JSONObject> objects = IntStream.range(0, getUpdatesList.length())
                .mapToObj(getUpdatesList::getJSONObject)
                .collect(toList());

        Optional<JSONObject> message = objects.stream().filter(it -> it.has("message"))
                .filter(it -> it.getJSONObject("message").has("forward_from_message_id"))
                .filter(it -> it.getJSONObject("message").getInt("forward_from_message_id") == channelPostId)
                .findFirst();

        if (message.isPresent()) {
         return TelegramMessage.Info.builder()
                 .id(message.get().getJSONObject("message").getInt("message_id"))
                 .name(message.get().getJSONObject("message").getJSONObject("chat").getString("username"))
                 .build();
         }else{
            throw new ServerException("Chat message not exit");
        }
    }

    public JSONArray getUpdates() {
        String getUpdatesUrl = format(GET_UPDATES_URL, telegramConfig.getToken());
        return Unirest.post(getUpdatesUrl)
                .asJson().getBody()
                .getObject().getJSONArray("result");
    }
}
