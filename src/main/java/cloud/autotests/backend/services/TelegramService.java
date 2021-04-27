package cloud.autotests.backend.services;

import cloud.autotests.backend.config.TelegramConfig;
import cloud.autotests.backend.models.Order;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cloud.autotests.backend.config.TelegramConfig.GET_UPDATES_URL;
import static cloud.autotests.backend.config.TelegramConfig.SEND_MESSAGE_URL;
import static java.lang.String.format;

@Service
@AllArgsConstructor
public class TelegramService {
    private static final Logger LOG = LoggerFactory.getLogger(TelegramService.class);

    private final TelegramConfig telegramConfig;

    public Integer createChannelPost(Order order, String issueKey) {
        String message = format(
                "<u><b>Test title</b></u>: <pre>%s</pre>\n" +
                        "<u><b>Price</b></u>: [%s]\n" +
                        "<u><b>Jira issue</b></u>: <a href=\"https://jira.autotests.cloud/browse/%s\">%s</a>\n",
                order.getTitle(), order.getPrice(), issueKey, issueKey); // todo email

        String body = format("chat_id=%s&text=%s&parse_mode=html", telegramConfig.getChannelId(), message);

        return sendText(body);
    }

    public Integer createChannelPost(Order order, String issueKey, String githubTestUrl) {
        String message = format(
                "<u><b>Test title</b></u>: <code>%s</code>\n" +
                        "<u><b>Price</b></u>: [%s]\n" +
                        "<u><b>Jira issue</b></u>: <a href=\"https://jira.autotests.cloud/browse/%s\">%s</a>\n" +
                        "<u><b>Jenkins job</b></u>: <a href=\"https://jenkins.autotests.cloud/job/%s\">%s</a>\n" +
                        "<u><b>Github code</b></u>:\n" +
                        "%s",
                order.getTitle(), order.getPrice(), issueKey, issueKey, issueKey, issueKey, githubTestUrl); // todo email

        String body = format("chat_id=%s&text=%s&parse_mode=html", telegramConfig.getChannelId(), message);

        return sendText(body);
    }

    public Integer addOnBoardingMessage(Integer chatMessageId) {
        String message = "Hello, my friend!\n\n" +
                "Jenkins job with tests <b>is already running</b>!\n" +
                "Report will be here <u>in 1 minute</u>!\n\n" +
                "Leave here any message to get notified";

        return sendText(format("chat_id=%s&reply_to_message_id=%s&text=%s&parse_mode=html",
                telegramConfig.getChatId(), chatMessageId, message));
    }

    private Integer sendText(String body) {
        String sendMessageUrl = format(SEND_MESSAGE_URL, telegramConfig.getToken());
        JSONObject createMessageResponse = Unirest.post(sendMessageUrl)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
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

        boolean messageResponse = createMessageResponse.getBoolean("ok");
        if (messageResponse)
            return createMessageResponse.getJSONObject("result")
                    .getInt("message_id");
        return null;
    }

    public Integer getChatMessageId(Integer channelPostId) {
        for (Object obj : getUpdates()) {
            JSONObject postObject = (JSONObject) obj;

            if (postObject.has("message")) {
                JSONObject messageObject = postObject.getJSONObject("message");
                if (messageObject.has("forward_from_message_id")) {
                    if (messageObject.getInt("forward_from_message_id") == channelPostId) {
                        return messageObject.getInt("message_id");
                    }
                }
            }
        }
        return null;
    }

    public JSONArray getUpdates() {
        String getUpdatesUrl = format(GET_UPDATES_URL, telegramConfig.getToken());
        return Unirest.post(getUpdatesUrl)
                .asJson().getBody()
                .getObject().getJSONArray("result");
    }
}
