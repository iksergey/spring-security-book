## Опишем SecurityConfig

Настройка безопасность для Spring Boot приложения

В пакете `configuration` опишем класс `SecurityConfig`

Класс `SecurityConfig` отвечает за конфигурацию безопасности в Spring-приложении. Он настраивает аутентификацию и авторизацию, определяя правила доступа к различным эндпоинтам.

Параметры конфигурации:
- Настройка JWT-аутентификации с использованием `JwtAuthenticationFilter`
- Определение `PasswordEncoder` для шифрования паролей
- Конфигурация `AuthenticationProvider` для проверки учётных данных
- Настройка `SecurityFilterChain` для определения правил доступа к ресурсам
- Отключение CSRF и настройка CORS [подробнее](./step-08-csrf-cors.md)

- Определение публичных эндпоинтов и эндпоинтов, требующих авторизации
- Настройка stateless сессий для работы с JWT (означает конфигурацию приложения таким образом, чтобы оно не хранило состояние сессии на сервере, а вместо этого использовало JWT для аутентификации и авторизации пользователей)
- Обработка исключений аутентификации

Этот класс обеспечивает безопасность приложения, интегрируя различные компоненты системы аутентификации и авторизации.

```java
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import ru.ksergey.ContactsApp.enums.AppRole;
import ru.ksergey.ContactsApp.service.jwt.AppUserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter authenticationFilter;
    private final AppUserService appUserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(appUserService.getDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(req -> req
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html")
                .permitAll()
                .requestMatchers("/api/admin/**").hasAuthority(AppRole.ADMIN.name())
                .requestMatchers("/api/user/**").hasAuthority(AppRole.USER.name())
                .anyRequest().authenticated()
            )
            .sessionManagement(m -> m.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider())
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
            );

        return httpSecurity.build();
    }
}
```

[Шаг 9](./step-09.md)
