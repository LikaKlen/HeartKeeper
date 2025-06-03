package com.example.HeattKeeper.service;

import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.dto.SettingsDto;
import com.example.HeattKeeper.dto.SupportRequestDto;
import com.example.HeattKeeper.models.Settings;
import com.example.HeattKeeper.models.SupportRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final DataAccessLayer dal;

    public Settings getSettings(Long userId) {
        try (Session session = dal.getSessionFactory().openSession()) {
            Settings settings = session.get(Settings.class, userId);
            if (settings == null) {
                settings = new Settings();
                settings.setUserId(userId);
                settings.setTheme("light");
                settings.setLanguage("ru");

                session.beginTransaction();
                session.persist(settings);
                session.getTransaction().commit();
            }
            return settings;
        }
    }

    public Settings updateSettings(Long userId, SettingsDto dto) {
        try (Session session = dal.getSessionFactory().openSession()) {
            session.beginTransaction();

            Settings settings = session.get(Settings.class, userId);
            if (settings == null) {
                settings = new Settings();
                settings.setUserId(userId);
            }

            settings.setTheme(dto.getTheme());
            settings.setLanguage(dto.getLanguage());

            session.saveOrUpdate(settings);
            session.getTransaction().commit();
            return settings;
        }
    }

    public void sendSupportRequest(SupportRequestDto dto) {
        try (Session session = dal.getSessionFactory().openSession()) {
            session.beginTransaction();

            SupportRequest request = new SupportRequest();
            request.setEmail(dto.getEmail());
            request.setMessage(dto.getMessage());

            session.persist(request);
            session.getTransaction().commit();
        }
    }
}