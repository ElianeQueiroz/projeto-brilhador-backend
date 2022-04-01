package com.brilhador.project.controllers;
import com.brilhador.project.models.base.User;
import com.brilhador.project.models.dto.AuthCredentials;
import com.brilhador.project.models.dto.AuthTokens;
import com.brilhador.project.models.dto.TokenInput;
import com.brilhador.project.models.dto.UserResponse;
import com.brilhador.project.models.dto.ValidationResponse;
import com.brilhador.project.services.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication")
@RestController @RequestMapping(path = "")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/sign-in")
    ResponseEntity<AuthTokens> signIn(AuthCredentials credentials) {
        try {
            Authentication authenticate = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(
                credentials.getEmail(), credentials.getPassword()
            ));
            
            User user = (User) authenticate.getPrincipal();
            AuthTokens tokens = authService.generateTokens(user);
            return ResponseEntity.ok().body(tokens);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        } 
    }

    @PostMapping("/sign-up")
    ResponseEntity<UserResponse> signUp(User user) {
        UserResponse response = authService.signUp(user).toUserResponse();
        return ResponseEntity.ok().body(response);
    } 

    @PostMapping("/validate-token")
    ResponseEntity<ValidationResponse> validateToken(TokenInput input) {
        ResponseEntity<ValidationResponse> unauthorized = ResponseEntity.status(401).build();
        try {
            String token = input.getToken();

            if (token.isBlank() || token.isEmpty()) return unauthorized;
            
            ValidationResponse response = authService.validateToken(token);
            if (response.isValid()) return ResponseEntity.ok().body(response);

            else return unauthorized;
        } catch (Exception e) {
            return unauthorized;
        }
    }
}
