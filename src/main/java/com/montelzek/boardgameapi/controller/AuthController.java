package com.montelzek.boardgameapi.controller;

import com.montelzek.boardgameapi.dto.AuthResponse;
import com.montelzek.boardgameapi.dto.LoginDTO;
import com.montelzek.boardgameapi.dto.RegisterDTO;
import com.montelzek.boardgameapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
