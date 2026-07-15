package com.panwar2001.reviewbot;

import org.springframework.stereotype.Service;

@Service
public class AiService {
    String getReview(String diff){
        return "reviewed";
    }
}
