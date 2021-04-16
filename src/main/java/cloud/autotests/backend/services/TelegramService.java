package cloud.autotests.backend.services;

import cloud.autotests.backend.config.TelegramConfig;
import cloud.autotests.backend.models.Order;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class TelegramService {

    @Autowired
    TelegramConfig telegramConfig;

    public Integer notifyOrder(Order order, String issueKey) {
        return sendText(String.format("<u><b>Issue</b></u>: <a href=\"https://jira.autotests.cloud/browse/%s\">%s</a> \n" +
                        "<u><b>Price</b></u>: %s\n" +
                        "<u><b>Email</b></u>: %s\n\n" +
                        "<u><b>Test title</b></u>: \n" +
                        "<pre>%s</pre>",
                issueKey, issueKey, order.getPrice(), order.getEmail(), order.getTitle()));
    }

    private Integer sendText(String message) {
        String body = String.format("chat_id=%s&text=%s&parse_mode=html", telegramConfig.telegramChatId, message);
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", telegramConfig.telegramToken);

        JSONObject createMessageResponse = Unirest.post(url)
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
