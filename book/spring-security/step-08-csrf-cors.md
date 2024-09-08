# CSRF и CORS

В контексте конфигурации безопасности, CSRF и CORS - это важные механизмы защиты:

## CSRF (Cross-Site Request Forgery)

CSRF - это тип атаки, при которой злоумышленник заставляет аутентифицированного пользователя выполнить нежелательное действие на веб-сайте без его ведома. 

В Spring Security CSRF-защита включена по умолчанию, но часто отключается для REST API, использующих токены

Отключение CSRF уместно, когда:
- Используется токенная аутентификация (например, JWT)
- API не предназначено для использования в браузере

## CORS (Cross-Origin Resource Sharing)

CORS - это механизм, позволяющий веб-приложениям на одном домене делать запросы к ресурсам на другом домене.

В Spring Security CORS настраивается так:

```java
http.cors()
```

Это позволяет настроить, каким доменам разрешено делать запросы к вашему API. Типичная конфигурация:

```java
@Bean
public CorsConfigurationSource corsConfig() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://allowed-origin.com"));
    configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

Правильная настройка CORS важна для:
- Разрешения доступа только доверенным доменам
- Определения разрешенных HTTP-методов
- Управления передачей учетных данных в cross-origin запросах

В контексте безопасного REST API, отключение CSRF и настройка CORS - это стандартные практики, обеспечивающие баланс между безопасностью и функциональностью.

[\<\< Назад](./step-08.md)
