package cloud.autotests.backend.services;

import cloud.autotests.backend.config.TelegramConfig;
import cloud.autotests.backend.models.Order;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TelegramService {
    private static final Logger LOG = LoggerFactory.getLogger(TelegramService.class);

    private final String SEND_MESSAGE_URL = "https://api.telegram.org/bot%s/sendMessage";
    private final String GET_UPDATES_URL = "https://api.telegram.org/bot%s/getUpdates";

    private final String channelId;
    private final String chatId;
    private final String sendMessageUrl;
    private final String getUpdatesUrl;

    @Autowired
    public TelegramService(TelegramConfig telegramConfig) {
        this.channelId = telegramConfig.getTelegramChannelId();
        this.chatId = telegramConfig.getTelegramChatId();
        this.sendMessageUrl = String.format(SEND_MESSAGE_URL, telegramConfig.telegramToken);
        this.getUpdatesUrl = String.format(GET_UPDATES_URL, telegramConfig.telegramToken);
    }

    public Integer createChannelPost(Order order, String issueKey) {
        String message = String.format(
                "<u><b>Test title</b></u>: <pre>%s</pre>\n" +
                        "<u><b>Price</b></u>: [%s]\n" +
                        "<u><b>Jira issue</b></u>: <a href=\"https://jira.autotests.cloud/browse/%s\">%s</a>\n",
                order.getTitle(), order.getPrice(), issueKey, issueKey); // todo email

        String body = String.format("chat_id=%s&text=%s&parse_mode=html", this.channelId, message);

        return sendText(body);
    }

    public Integer createChannelPost(Order order, String issueKey, String githubTestUrl) {
        String message = String.format(
                "<u><b>Test title</b></u>: <code>%s</code>\n" +
                        "<u><b>Price</b></u>: [%s]\n" +
                        "<u><b>Jira issue</b></u>: <a href=\"https://jira.autotests.cloud/browse/%s\">%s</a>\n" +
                        "<u><b>Jenkins job</b></u>: <a href=\"https://jenkins.autotests.cloud/job/%s\">%s</a>\n" +
                        "<u><b>Github code</b></u>:\n" +
                        "%s",
                order.getTitle(), order.getPrice(), issueKey, issueKey, issueKey, issueKey, githubTestUrl); // todo email

        String body = String.format("chat_id=%s&text=%s&parse_mode=html", this.channelId, message);

        return sendText(body);
    }

    public Integer addOnBoardingMessage(Integer chatMessageId) {
        String message = "Hello, my friend!\n\n" +
                "Jenkins job with tests is already running!\n" +
                "Report will be here in 1 minute!\n\n" +
                "Leave here any message to get notified";

        return sendText(String.format("chat_id=%s&reply_to_message_id=%s&text=%s&parse_mode=html",
            this.chatId, chatMessageId, message));
    }

    private Integer sendText(String body) {
        JSONObject createMessageResponse = Unirest.post(this.sendMessageUrl)
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
        return Unirest.post(getUpdatesUrl)
                .asJson().getBody()
                .getObject().getJSONArray("result");
    }
}
