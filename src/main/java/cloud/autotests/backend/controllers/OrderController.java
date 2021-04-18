package cloud.autotests.backend.controllers;

import cloud.autotests.backend.models.Order;
import cloud.autotests.backend.services.GithubService;
import cloud.autotests.backend.services.JiraService;
import cloud.autotests.backend.services.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.Thread.sleep;

@RestController
@RequestMapping("orders")
public class OrderController {

    @Autowired
    JiraService jiraService;

    @Autowired
    GithubService githubService;

    @Autowired
    TelegramService telegramService;

    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {

        return new ResponseEntity<>(List.of(new Order(), new Order()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createOrder(@RequestBody Order order) throws InterruptedException {
//        String jiraIssueKey = jiraService.createTask(order);
//        if (jiraIssueKey == null) {
//            return new ResponseEntity<>("Cant create jira issue", HttpStatus.INTERNAL_SERVER_ERROR);
//        }

        String githubRepositoryUrl = githubService.createRepositoryFromTemplate("TM-143");
//        String githubRepositoryUrl = githubService.createRepoFromTemplate(jiraIssueKey);
        if (githubRepositoryUrl == null) {
            return new ResponseEntity<>("Cant create github repository", HttpStatus.INTERNAL_SERVER_ERROR);
        }

//        Integer telegramChannelPostId = telegramService.createChannelPost(order, jiraIssueKey);
//        if (telegramChannelPostId == null) {
//            return new ResponseEntity<>("Cant create telegram channel post", HttpStatus.INTERNAL_SERVER_ERROR);
//        }

//        sleep(1000);
//        Integer onboardingMessageId = telegramService.addOnboardingMessage(channelPostId);
//        if (onboardingMessageId == null) {
//            return new ResponseEntity<>("Cant add comment to telegram channel post", HttpStatus.INTERNAL_SERVER_ERROR);
//        }

        return new ResponseEntity<>(githubRepositoryUrl, HttpStatus.CREATED);
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
