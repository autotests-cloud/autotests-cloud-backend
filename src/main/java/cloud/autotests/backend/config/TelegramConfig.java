package cloud.autotests.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TelegramConfig {
    @Value("${telegram.token}")
    public String telegramToken;

    @Value("${telegram.channel.id}")
    public String telegramChannelId;

    @Value("${telegram.chat.id}")
    public String telegramChatId;
}
