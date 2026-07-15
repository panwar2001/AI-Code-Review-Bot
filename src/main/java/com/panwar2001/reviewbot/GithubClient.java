package com.panwar2001.reviewbot;

import org.springframework.stereotype.Service;

@Service
public class GithubClient {
   public String getFullDiff(String repo, Integer prNumber){
       return "";
   }
    public String getDiffBetweenCommits(
            String repo,
            String beforeSha,
            String afterSha
    ){
        return "";
    }
    public void pushReview(String review, Integer prNumber){

    }
}
