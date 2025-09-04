package com.rulesengine.app.controller;

import com.rulesengine.app.service.AuthService;
import com.rulesengine.model.dtos.AuthRequest;
import com.rulesengine.model.dtos.JwtResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    // A simple registration endpoint for creating users.
    // In a real app, this might be an admin-only feature.
    // @PostMapping("/register")
    // public ResponseEntity<?> register(@RequestBody User user) {
    //     authService.register(user);
    //     return ResponseEntity.ok("User registered successfully");
    // }
}

