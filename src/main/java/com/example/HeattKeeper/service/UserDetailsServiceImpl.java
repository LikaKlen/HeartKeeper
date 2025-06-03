package com.example.HeattKeeper.service;

import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.dto.EditUser;
import com.example.HeattKeeper.dto.SignUp;
import com.example.HeattKeeper.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final DataAccessLayer dataAccessLayer;
    private final ChatService chatService;

    @Autowired
    public UserDetailsServiceImpl(DataAccessLayer dataAccessLayer, ChatService chatService) {
        this.dataAccessLayer = dataAccessLayer;
        this.chatService = chatService;
    }
    public User findByEmail(String email) {
        return dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public String newUser(SignUp signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword());
        return dataAccessLayer.newUserToDatabase(user);
    }

    public User loadUserEntityByUsername(String email) {
        return dataAccessLayer.getUserFromDatabaseByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = loadUserEntityByUsername(username);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    public User findUser(Long id) {
        return dataAccessLayer.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public User editUser(Long id, EditUser editUser) {
        User user = findUser(id);
        user.setUsername(editUser.getUsername());
        user.setEmail(editUser.getEmail());
        dataAccessLayer.updateUser(user);
        return user;
    }

    public String generateInvitationCode(Long id) {
        User user = findUser(id);
        if (user.getPartner() != null) {
            throw new RuntimeException("У пользователя уже есть партнер");
        }
        // Generate new code even if one exists (for regeneration)
        String code = UUID.randomUUID().toString().substring(0, 8);
        user.setInvitationCode(code);
        dataAccessLayer.updateUser(user);
        return code;
    }

    public User assignPartner(Long userId, String invitationCode) {
        User user = findUser(userId);

        if (user.getPartner() != null) {
            throw new RuntimeException("У вас уже есть партнер");
        }

        User partner = dataAccessLayer.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new RuntimeException("Неверный код"));

        if (partner.getPartner() != null) {
            throw new RuntimeException("Этот пользователь уже имеет партнера");
        }

        if (partner.getId().equals(user.getId())) {
            throw new RuntimeException("Нельзя добавить себя в качестве партнера");
        }

        user.setPartner(partner);
        partner.setPartner(user);
        user.setInvitationCode(null);
        partner.setInvitationCode(null);

        dataAccessLayer.updateUser(user);
        dataAccessLayer.updateUser(partner);
        chatService.createChat(user, partner);

        return user;
    }

    public void removePartner(Long userId) {
        User user = findUser(userId);
        User partner = user.getPartner();

        if (partner != null) {
            user.setPartner(null);
            partner.setPartner(null);

            // Generate new invitation code for both users
            user.setInvitationCode(UUID.randomUUID().toString().substring(0, 8));
            partner.setInvitationCode(UUID.randomUUID().toString().substring(0, 8));

            dataAccessLayer.updateUser(user);
            dataAccessLayer.updateUser(partner);
        }
    }
}
