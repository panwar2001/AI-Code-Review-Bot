package com.panwar2001.reviewbot;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class ReviewService {
    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);
    private final GithubClient githubClient;
    private final StringRedisTemplate redisTemplate;
    private final AiService aiService;
    public ReviewService(GithubClient githubClient, StringRedisTemplate redisTemplate, AiService aiService) {
        this.githubClient = githubClient;
        this.redisTemplate = redisTemplate;
        this.aiService = aiService;
    }

    @Async
 public void processReview(
         String deliveryId,
         Map<String, Object> payload,
         String action
    ){
        if (isDuplicate(deliveryId)) {
            log.info("Duplicate webhook delivery detected and ignored: {}", deliveryId);
            return;
        }
        try {
            Integer prNumber = (Integer) payload.get("number");
            log.info("Starting review for PR #{} with action: {}", prNumber, action);
            String diff = getDiff(payload, action, prNumber);
            if (diff == null || diff.isEmpty()) {
                log.info("No changes found for PR #{}. Skipping AI review.", prNumber);
                return;
            }
            String aiReview = aiService.getReview(diff);
            githubClient.pushReview(aiReview, prNumber);
            log.info("Successfully completed review for PR #{}", prNumber);
        }catch (Exception e){
            log.error("Failed to process review for PR: {}", payload.get("number"), e);
        }
 }
 private String getDiff(
         Map<String, Object> payload,
         String action,
         Integer prNumber
 ) {
     Map<String, Object> repo = (Map<String, Object>) payload.get("repository");
     String repoFullName = (String) repo.get("full_name");
     // 4. Extract Before and After SHAs
     // Note: 'before' and 'after' are in the root of the push/synchronize payload
     String beforeSha = (String) payload.get("before");
     String afterSha = (String) payload.get("after");
     return switch (action) {
         case "opened" -> githubClient.getFullDiff(repoFullName, prNumber);
         case "synchronize" -> {
             // Safety check: if before is null or zeros, treat as initial pull
             if (beforeSha == null || beforeSha.equals("0000000000000000000000000000000000000000")) {
                 yield githubClient.getFullDiff(repoFullName, prNumber);
             }
             // Optionally fetch only the changes since the last push
             yield githubClient.getDiffBetweenCommits(repoFullName, beforeSha, afterSha);
         }
         default -> {
             ReviewService.log.warn("Unrecognized action: {}", action);
             yield ""; // Return empty to skip processing
         }
     };
 }
    private boolean isDuplicate(String deliveryId) {
        // setIfAbsent returns true if key was set (new), false if key already existed (duplicate)
        // We use Boolean.FALSE.equals to handle nulls safely
        Boolean isNew = redisTemplate.opsForValue()
                .setIfAbsent("webhook_id:" + deliveryId, "PROCESSED", Duration.ofHours(24));

        return !isNew;
    }
}
