package com.example.HeattKeeper.service;

import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.dto.EditUser;
import com.example.HeattKeeper.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final DataAccessLayer dataAccessLayer;

    @Autowired
    public ProfileService(DataAccessLayer dataAccessLayer) {
        this.dataAccessLayer = dataAccessLayer;
    }
    public void updateUserAvatar(String email, String imageUrl) {
        dataAccessLayer.updateUserAvatarByEmail(email, imageUrl);
    }

    public EditUser getProfile(String email) {
        User user = dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        EditUser dto = new EditUser();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setProfilePhotoUrl(user.getProfilePhotoUrl());
        dto.setBackgroundUrl(user.getBackgroundUrl());
        return dto;
    }

    public EditUser updateProfile(String email, EditUser dto) {
        User user = dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        user.setUsername(dto.getUsername());
        user.setProfilePhotoUrl(dto.getProfilePhotoUrl());
        user.setBackgroundUrl(dto.getBackgroundUrl());
        dataAccessLayer.updateUser(user);
        return getProfile(email);
    }
}