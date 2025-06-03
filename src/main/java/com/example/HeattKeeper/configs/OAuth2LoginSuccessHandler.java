package com.example.HeattKeeper.configs;

import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.models.AuthProvider;
import com.example.HeattKeeper.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final DataAccessLayer dataAccessLayer;

    public OAuth2LoginSuccessHandler(DataAccessLayer dataAccessLayer) {
        this.dataAccessLayer = dataAccessLayer;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oAuthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuthUser.getAttribute("email");
        String name = oAuthUser.getAttribute("name");
        String picture = oAuthUser.getAttribute("picture");

        Optional<User> optionalUser = dataAccessLayer.getUserFromDatabaseByEmail(email);
        if (optionalUser.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setProfilePhotoUrl(picture);
            newUser.setAuthProvider(AuthProvider.GOOGLE);

            String result = dataAccessLayer.newUserToDatabase(newUser);
            System.out.println("OAuth2 user creation: " + result);
        }

        response.sendRedirect("http://localhost:5173");
    }
}