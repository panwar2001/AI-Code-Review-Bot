package com.panwar2001.reviewbot;

import com.panwar2001.reviewbot.request.WebhookPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebhookController {
    private final ReviewService reviewService;

    public WebhookController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    public ResponseEntity<String> handleWebhook(@RequestBody WebhookPayload payload){

        return ResponseEntity.accepted().body("Review process initiated!");
    }
}
