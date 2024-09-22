# Гайд по конфигурированию проекта

❗️ Перед обновлением создайте отдельную ветку с вашим приложением

### Шаг 1: Изменение базового пакета

Первым делом нам нужно изменить базовый пакет проекта с `ContactsApp` на `SecurityApp`. Это поможет устранить путаницу и лучше отразить назначение нашего приложения.

### Шаг 2: Добавление нового контроллера

Теперь мы добавим новый контроллер в пакет `controllers`. Этот контроллер будет отвечать за валидацию токенов.

```java
@RestController
@RequiredArgsConstructor
public class TokenValidationController {

    private final JwtSecurityService jwtSecurityService;

    @PostMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        boolean isValid = jwtSecurityService.validateTokenFromHeader(authHeader);
        return ResponseEntity.ok(isValid);
    }
}
```

**Пояснения:**
- `@PostMapping("/validate-token")`: Определяет эндпоинт для POST-запросов по пути "/validate-token".
- `@RequestHeader("Authorization")`: Извлекает значение заголовка "Authorization" из входящего запроса.

### Шаг 3: Обновление сервиса JwtSecurityService

Теперь добавим новый метод `validateTokenFromHeader` в сервис `JwtSecurityService`:

```java
@Service
@RequiredArgsConstructor
public class JwtSecurityService {

    private final AppUserService appUserService;

    // Остальной код без изменений

    public boolean validateTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);

        try {
            String username = extractUsername(token);
            UserDetails userDetails = appUserService
                    .getDetailsService()
                    .loadUserByUsername(username);
            return validateToken(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }
}
```

**Пояснения:**
- Метод `validateTokenFromHeader` проверяет валидность токена, полученного из заголовка запроса.
- Он извлекает токен из заголовка, затем извлекает имя пользователя из токена и проверяет его валидность.
- Если возникают какие-либо исключения в процессе валидации, метод возвращает `false`.

### Шаг 4: Настройка базы данных

В файле `application.properties` нужно внести изменения в раздел с указанием пути к базе данных. У вас есть два варианта:

1. Поднять докер-контейнер с базой данных, указав порт, отличный от стандартного.
    ```
    spring.datasource.url=jdbc:mysql://localhost:4445/your_database_name
    ```
2. Использовать одну базу данных для двух микросервисов (плохая практика), создав схему, отличную от `contacts_db` (т е её использует приложение с контактами).

```
spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
```
