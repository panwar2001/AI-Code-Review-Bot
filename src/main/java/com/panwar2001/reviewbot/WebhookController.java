package com.panwar2001.reviewbot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class WebhookController {
    private final ReviewService reviewService;

    public WebhookController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader("X-GitHub-Delivery") String deliveryId,
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestBody Map<String, Object> payload
    ){
        if (!"pull_request".equals(eventType)) {
            return ResponseEntity.ok("Ignored non-PR event");
        }
        String action = (String) payload.get("action");
        /*
        pull_request -> opened: Triggers when the PR is first created.
        pull_request -> synchronize: Triggers whenever new commits are pushed to the PR branch.
        */
        if ("opened".equals(action) || "synchronize".equals(action)) {
            reviewService.processReview(deliveryId, payload, action);
        }
        return ResponseEntity.accepted().body("Review process initiated!");
    }
}
