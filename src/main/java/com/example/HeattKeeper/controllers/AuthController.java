package com.example.HeattKeeper.controllers;

import com.example.HeattKeeper.HeattKeeperApplication;
import com.example.HeattKeeper.dal.DataAccessLayer;
import com.example.HeattKeeper.dto.*;
import com.example.HeattKeeper.models.AuthProvider;
import com.example.HeattKeeper.models.User;

import com.example.HeattKeeper.security.JwtCore;
import com.example.HeattKeeper.security.TokenStorage;
import com.example.HeattKeeper.service.GoogleTokenVerifier;
import com.example.HeattKeeper.service.UserDetailsServiceImpl;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private UserDetailsServiceImpl userService;
    @Autowired
    private JwtCore jwtCore;
    @Autowired
    private PasswordEncoder passwordEncoder; // Теперь инициализируется через конструктор
    @Autowired
    private TokenStorage tokenStorage;
    @Autowired
    private DataAccessLayer dataAccessLayer;
    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUp signup) {
        if (signup.getUsername() == null || signup.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Введите имя");
        }
        if (signup.getEmail() == null || signup.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Введите почту");
        }
        if (signup.getPassword() == null || signup.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Введите пароль");
        }

        String serviceResult = userService.newUser(signup);

        if ("Выберите другое имя".equals(serviceResult) || "Выберите другую почту".equals(serviceResult)) {
            return ResponseEntity.badRequest().body(serviceResult);
        }

        return ResponseEntity.ok("Вы успешно зарегистрированы. Теперь можете войти в свой аккаунт.");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignIn signIn) {
        if (signIn.getEmail() == null || signIn.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Введите почту");
        }
        if (signIn.getPassword() == null || signIn.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Введите пароль");
        }

        User user = userService.loadUserEntityByUsername(signIn.getEmail());
        ResponseEntity.ok("Вы успешно вошли.");

        if (user == null || !passwordEncoder.matches(signIn.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный email или пароль");
        }

        JwtTokenResponse jwt = jwtCore.generateJWTResponse(user);


        return ResponseEntity.ok(jwt);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        String email = jwtCore.validateRefreshTokenAndRetrieveEmail(refreshToken);
        User user = userService.findByEmail(email); // реализуйте метод
        tokenStorage.invalidateToken(user.getId());
        return ResponseEntity.ok("Logged out successfully.");
    }
    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@RequestBody GoogleAuthRequest request) {
        try {
            GoogleIdToken.Payload payload = googleTokenVerifier.verifyToken(request.getCredential());

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            Optional<User> optionalUser = dataAccessLayer.getUserFromDatabaseByEmail(email);
            User user;

            if (optionalUser.isEmpty()) {
                user = new User();
                user.setEmail(email);
                user.setUsername(name);
                user.setProfilePhotoUrl(picture);
                user.setAuthProvider(AuthProvider.GOOGLE);
                dataAccessLayer.newUserToDatabase(user);
            } else {
                user = optionalUser.get();
            }

            JwtTokenResponse tokenResponse = jwtCore.generateJWTResponse(user);

            return ResponseEntity.ok(new AuthResponse(
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    user.getId(),
                    user.getUsername(),
                    user.getProfilePhotoUrl()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google authentication failed");
        }
    }
}