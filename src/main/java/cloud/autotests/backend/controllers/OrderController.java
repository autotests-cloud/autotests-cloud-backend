package cloud.autotests.backend.controllers;

import cloud.autotests.backend.models.Order;
import cloud.autotests.backend.services.JiraService;
import cloud.autotests.backend.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
public class OrderController {

    @Autowired
    TelegramService telegramService;
    JiraService jiraService;

    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {

        return new ResponseEntity<>(List.of(new Order(), new Order()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createOrder(@RequestBody Order order) {
        String issueKey = jiraService.createTask(order);
        if (issueKey == null) {
            return new ResponseEntity<>("Cant create jira issue", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Integer messageId = telegramService.notifyOrder(order, issueKey);
        if (messageId == null) {
            return new ResponseEntity<>("Cant send telegram message", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(messageId, HttpStatus.CREATED);
    }

    @GetMapping("/{order}")
    public ResponseEntity<Order> getOrderById(@PathVariable String order) {
        // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(new Order(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Order> updateOrder(@RequestBody Order order) {

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @DeleteMapping("/{order}")
    public ResponseEntity deleteOrder(@PathVariable String order) {

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
