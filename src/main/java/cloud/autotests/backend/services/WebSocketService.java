package cloud.autotests.backend.services;

import cloud.autotests.backend.models.WebsocketMessage;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import static java.lang.Thread.sleep;

@Service
public class WebSocketService {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketService.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @SneakyThrows
    public void sendMessage(String uniqueUserId, WebsocketMessage websocketMessage) {
        sleep(1000);
        messagingTemplate.convertAndSend("/topic/" + uniqueUserId, websocketMessage);
    }
}
