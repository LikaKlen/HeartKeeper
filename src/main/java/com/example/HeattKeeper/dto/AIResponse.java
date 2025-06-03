package com.example.HeattKeeper.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public  class AIResponse {
    private String response;
    private List<String> suggestions;
    private Map<String, List<String>> followUp;
    private Map<String, Object> metadata;
    public AIResponse(){}
    public AIResponse(String response, List<String> suggestions, Map<String, List<String>> followUp, Map<String, Object> metadata) {
        this.response = response;
        this.suggestions = suggestions;
        this.followUp = followUp;
        this.metadata = metadata;
    }

    // Getters and setters
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public Map<String, List<String>> getFollowUp() { return followUp; }
    public void setFollowUp(Map<String, List<String>> followUp) { this.followUp = followUp; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}