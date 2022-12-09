package cloud.autotests.backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@Getter
@ConfigurationProperties(prefix = "telegram")
public class TelegramConfig {

    public static final String SEND_MESSAGE_URL = "https://api.telegram.org/bot%s/sendMessage";
    public static final String GET_UPDATES_URL = "https://api.telegram.org/bot%s/getUpdates";
    public static final String TELEGRAM_DISCUSSION_URL_TEMPLATE = "https://t.me/%s/%s?comment=%s";

    @Value("${telegram.token}")
    public String token;

    @Value("${telegram.channel.id}")
    public String channelId;

    @Value("${telegram.chat.id}")
    public String chatId;
}
