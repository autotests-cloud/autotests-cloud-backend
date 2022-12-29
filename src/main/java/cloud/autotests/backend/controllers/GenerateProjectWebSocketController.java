package cloud.autotests.backend.controllers;
import cloud.autotests.backend.models.request.GenerateRequest;
import cloud.autotests.backend.models.websocket.WebsocketMessage;
import cloud.autotests.backend.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class GenerateProjectWebSocketController {
    private final WebSocketService webSocketService;

    @MessageMapping("/orders/{uniqueUserId}")
    public void createOrder(@DestinationVariable("uniqueUserId") String uniqueUserId, @RequestBody @Valid GenerateRequest request) throws InterruptedException {
        webSocketService.generate(uniqueUserId, request);
    }

    @MessageMapping("/test/{uniqueUserId}")
    public void createOrderTest(@DestinationVariable("uniqueUserId") String uniqueUserId) {
        webSocketService.sendMessage(uniqueUserId,
                new WebsocketMessage()
                        .setPrefix("x")
                        .setContentType("error")
                        .setContent("test message"));
    }
}
