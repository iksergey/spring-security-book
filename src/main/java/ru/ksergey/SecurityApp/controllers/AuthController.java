package ru.ksergey.SecurityApp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.ksergey.SecurityApp.model.AppUser;
import ru.ksergey.SecurityApp.modelDto.LoginRequestDto;
import ru.ksergey.SecurityApp.modelDto.LoginResponseDto;
import ru.ksergey.SecurityApp.modelDto.RefreshTokenRequestDto;
import ru.ksergey.SecurityApp.modelDto.RefreshTokenResponseDto;
import ru.ksergey.SecurityApp.modelDto.RegisterRequestDto;
import ru.ksergey.SecurityApp.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody RegisterRequestDto registerRequestDto) {
        return ResponseEntity.ok(authService.register(registerRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refresh(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return ResponseEntity.ok(authService.refresh(refreshTokenRequestDto));
    }
}
