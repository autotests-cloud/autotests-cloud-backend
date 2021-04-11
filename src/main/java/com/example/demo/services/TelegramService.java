package com.example.demo.services;

import com.example.demo.config.TelegramConfig;
import com.example.demo.models.Order;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Service
public class TelegramService {

    @Autowired
    TelegramConfig telegramConfig;

    public void notifyOrder(Order order) {
        sendText(String.format("Here is new order. \n" +
                "Price: %s\n" +
                "Email: %s\n" +
                "Order message: %s",
                order.getPrice(), order.getEmail(), order.getContent()));
    }

    private void sendText(String message) {
        String body = String.format("chat_id=%s&text=%s&parse_mode=Markdown", telegramConfig.telegramChatId, message);
        String url = String.format("https://api.telegram.org/bot%s/sendMessage", telegramConfig.telegramToken);

        Unirest.post(url)
//                .routeParam("token", telegramConfig.telegramToken)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .body(body)
                .asString()
                .getBody();
    }
}
