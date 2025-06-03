package com.example.HeattKeeper.dto;

import lombok.Data;
import com.example.HeattKeeper.dto.Location;
import java.util.Map;
@Data
public class UserPreferences {
    private String language;
    private Integer relationshipDuration;
    private Map<String, Object> partnerPreferences;
    private Location location;

    public UserPreferences(String language, Integer relationshipDuration, Map<String, Object> partnerPreferences) {
        this.language = language;
        this.relationshipDuration = relationshipDuration;
        this.partnerPreferences = partnerPreferences;
    }

    // Getters and setters
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Integer getRelationshipDuration() { return relationshipDuration; }
    public void setRelationshipDuration(Integer relationshipDuration) { this.relationshipDuration = relationshipDuration; }
    public Map<String, Object> getPartnerPreferences() { return partnerPreferences; }
    public void setPartnerPreferences(Map<String, Object> partnerPreferences) { this.partnerPreferences = partnerPreferences; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
}