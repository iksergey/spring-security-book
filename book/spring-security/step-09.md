# Попытка регистрации

[Желательно, донастроить swagger](./step-09-swagger.md)

Опишем dto модели для разных запросов

- Регистрация пользователя

```java
@Data
public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String password;
}
```

- Логин пользователя

```java
@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
```

- Ответ на запрос логина

```java
@Data
@Builder
public class LoginResponseDto {
    private String email;
    private String jwtToken;
    private String refreshToken;
}
```

- "Сброс" токена

```java
@Data
public class RefreshTokenRequestDto {
    private String refreshToken;
}
```

- Ответ на "сброс" токена
```java
@Data
public class RefreshTokenResponseDto {
    private String jwtToken;
    private String refreshToken;
}
```

Опишем `AuthService`, предоставляет основные функции аутентификации и управления пользователями в приложении. Он включает в себя методы для регистрации, входа и обновления токенов.

Основные функции сервиса:

1. **Регистрация пользователя**:
   - Создает нового пользователя с зашифрованным паролем
   - Назначает роль `USER` по умолчанию
   - Сохраняет пользователя в базе данных

2. **Аутентификация пользователя**:
   - Проверяет учетные данные пользователя
   - Генерирует `JWT` токен и `refresh` токен
   - Возвращает данные для аутентифицированного пользователя

3. **Обновление токена**:
   - Проверяет валидность `refresh` токена
   - Генерирует новую пару `JWT` и `refresh` токенов

Сервис использует:
- `PasswordEncoder` для шифрования паролей
- `JwtSecurityService` для работы с `JWT` токенами
- `AuthenticationManager` для проверки учетных данных
- `AppUserRepository` для взаимодействия с базой данных пользователей

Ключевые моменты безопасности:
- Пароли хранятся в зашифрованном виде
- Используется механизм `JWT` для безопасной аутентификации
- Реализована система `refresh` токенов для продления сессий

```java
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import ru.ksergey.ContactsApp.enums.AppRole;
import ru.ksergey.ContactsApp.model.AppUser;
import ru.ksergey.ContactsApp.modelDto.LoginRequestDto;
import ru.ksergey.ContactsApp.modelDto.LoginResponseDto;
import ru.ksergey.ContactsApp.modelDto.RefreshTokenRequestDto;
import ru.ksergey.ContactsApp.modelDto.RefreshTokenResponseDto;
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

        Authentication authentication = authenticationManager.authenticate(
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

    public RefreshTokenResponseDto refresh(RefreshTokenRequestDto refreshTokenRequestDto) {
        String jwt = refreshTokenRequestDto.getRefreshToken();
        String email = jwtSecurityService.extractUsername(jwt);
        AppUser user = appUserRepository
                .findByEmail(email)
                .orElseThrow();

        if (jwtSecurityService.validateToken(jwt, user)) {
            RefreshTokenResponseDto refreshTokenResponseDto = new RefreshTokenResponseDto();

            refreshTokenResponseDto
                    .setJwtToken(
                            jwtSecurityService.generateToken(user));

            refreshTokenResponseDto
                    .setRefreshToken(
                            jwtSecurityService.generateRefreshToken(new HashMap<>(), user));

            return refreshTokenResponseDto;
        }

        return null;
    }
}

```

Добавим `AuthController` в котором опишем endpoint'ы

```java
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.ksergey.ContactsApp.model.AppUser;
import ru.ksergey.ContactsApp.modelDto.LoginRequestDto;
import ru.ksergey.ContactsApp.modelDto.LoginResponseDto;
import ru.ksergey.ContactsApp.modelDto.RefreshTokenRequestDto;
import ru.ksergey.ContactsApp.modelDto.RefreshTokenResponseDto;
import ru.ksergey.ContactsApp.modelDto.RegisterRequestDto;
import ru.ksergey.ContactsApp.service.AuthService;

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
```

### Протестируйте работу этого контроллера

[Замечание](./step-09-comment.md)

[Шаг 10](./step-10.md)
