package ru.ksergey.ContactsApp.service;

import java.util.HashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.ksergey.ContactsApp.enums.AppRole;
import ru.ksergey.ContactsApp.model.AppUser;
import ru.ksergey.ContactsApp.modelDto.LoginRequestDto;
import ru.ksergey.ContactsApp.modelDto.LoginResponseDto;
import ru.ksergey.ContactsApp.modelDto.RegisterRequestDto;
import ru.ksergey.ContactsApp.repository.AppUserRepository;
import ru.ksergey.ContactsApp.service.jwt.JwtSecurityService;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtSecurityService jwtSecurityService;
    private final AuthenticationManager authenticationManager;

    public AppUser register(RegisterRequestDto registerRequestDto) {
        AppUser appUser = new AppUser();
        appUser.setEmail(registerRequestDto.getEmail());
        appUser.setFullName(registerRequestDto.getFullName());
        appUser.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        appUser.setRole(AppRole.USER);

        return appUserRepository.save(appUser);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()));

        AppUser user = appUserRepository
                .findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Ошибка"));

        String token = jwtSecurityService.generateToken(user);
        String refreshToken = jwtSecurityService.generateRefreshToken(new HashMap<>(), user);

        return LoginResponseDto
                .builder()
                .email(loginRequestDto.getEmail())
                .jwtToken(token)
                .refreshToken(refreshToken)
                .build();
    }
}
