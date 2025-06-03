package com.example.HeattKeeper.configs;

import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.models.User;
import com.fasterxml.jackson.core.filter.TokenFilter;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfiguration {
    private final SessionFactory sessionFactory;

    @Autowired
    public DataConfiguration(EntityManager entityManager) {
        Session session = entityManager.unwrap(Session.class);
        this.sessionFactory = session.getSessionFactory();
    }

    @Bean
    public User user() {
        return new User();
    }

    @Bean
    public DataAccessLayer dataAccessLayer() {
        return new DataAccessLayer(sessionFactory);
    }


}