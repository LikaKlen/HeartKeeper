package com.example.HeattKeeper.service;

import com.example.HeattKeeper.dto.AIResponse;
import com.example.HeattKeeper.dto.UserPreferences;
import com.example.HeattKeeper.dto.WeeklyReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Service
public class RelationshipAIService {
    private final String PYTHON_API_URL = "http://localhost:5000";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIResponse getAIResponse(Long userId, String message, String context) {
        String url = PYTHON_API_URL + "/api/chat";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", userId);
        requestBody.put("message", message);
        requestBody.put("context", context);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), AIResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get AI response", e);
        }
    }

    public WeeklyReport getWeeklyReport(Long userId) {
        String url = PYTHON_API_URL + "/api/weekly_report";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return objectMapper.readValue(response.getBody(), WeeklyReport.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get weekly report", e);
        }
    }

    public void updateUserPreferences(Long userId, UserPreferences preferences) {
        String url = PYTHON_API_URL + "/api/update_preferences";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", userId);
        requestBody.put("language", preferences.getLanguage());
        requestBody.put("relationship_duration", preferences.getRelationshipDuration());
        requestBody.put("partner_quiz", preferences.getPartnerPreferences());

        if (preferences.getLocation() != null) {
            Map<String, String> location = new HashMap<>();
            location.put("city", preferences.getLocation().getCity());
            location.put("country", preferences.getLocation().getCountry());
            requestBody.put("location", location);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user preferences", e);
        }
    }
}
