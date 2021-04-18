package cloud.autotests.backend.services;

import cloud.autotests.backend.config.TelegramConfig;
import cloud.autotests.backend.models.Order;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class TelegramService {

    private String channelId;
    private String chatId;
    private String sendMessageUrl;

    @Autowired
    public TelegramService(TelegramConfig telegramConfig) {
        this.channelId = telegramConfig.getTelegramChannelId();
        this.chatId = telegramConfig.getTelegramChatId();
        this.sendMessageUrl = String.format("https://api.telegram.org/bot%s/sendMessage", telegramConfig.telegramToken);
    }

    public Integer createChannelPost(Order order, String issueKey) {
        String message = String.format("<u><b>Issue</b></u>: <a href=\"https://jira.autotests.cloud/browse/%s\">%s</a> \n" +
                        "<u><b>Price</b></u>: %s\n" +
                        "<u><b>Email</b></u>: %s\n\n" +
                        "<u><b>Test title</b></u>: \n" +
                        "<pre>%s</pre>",
                issueKey, issueKey, order.getPrice(), order.getEmail(), order.getTitle());

        String body = String.format("chat_id=%s&text=%s&parse_mode=html", this.channelId, message);

        return sendText(body);
    }

    public Integer addOnboardingMessage(Integer channelPostId) {
        String message = String.format("Hello, my friend!\n\n" +
                        "Leave any message here, to get notified, when autotests get ready!");

        String body = String.format("chat_id=%s&reply_to_message_id=%s&text=%s&parse_mode=html",
                channelPostId, this.chatId, message);

        return sendText(body);
    }

    private Integer sendText(String body) {
        JSONObject createMessageResponse = Unirest.post(this.sendMessageUrl)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .body(body)
                .asJson()
                .getBody()
                .getObject();

        boolean messageResponse = createMessageResponse.getBoolean("ok");
        if (messageResponse)
            return createMessageResponse.getJSONObject("result")
                    .getInt("message_id");
        return null;
    }
}
