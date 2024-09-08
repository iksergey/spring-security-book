# Описание сервиса для работы с JWT

В пакете `service.jwt` опишем сервис `JwtSecurityService`

Класс `JwtSecurityService` предоставляет функциональность для работы с `JSON Web Tokens` в контексте безопасности приложения. Он отвечает за генерацию, валидацию и извлечение информации из `JWT` токенов.

Основные функции класса включают:
- Генерацию токенов доступа и обновления
- Извлечение данных из токенов (имя пользователя, срок действия)
- Проверку валидности токенов
- Работу с секретным ключом для подписи токенов

Класс использует библиотеку `JJWT` для операций с JWT и интегрируется с `UserDetails` из Spring Security для аутентификации пользователей. Это ключевой компонент в реализации JWT-аутентификации в Spring-приложении.

```java
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.io.Decoders;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtSecurityService {

    private static final String SECRET_KEY = "6yU3AaLTrj/YSKQtYF6yU3/YSKAaLTIv9aRtGxOcU39h7T/aRtGxO+syA=";

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                // .claim("field", "value") // Добавление других нужных полей в токен
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Map<String, String> claims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Получение имени юзера (он же почта)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Когда срок действия заканчивается
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Когда выдан токен
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    // Метод проверки срока действия токена
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Метод для валидации токена
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }
}
```

Перейдя на [jwt.io](https://jwt.io) можете убедиться в валидности токена проверив его подпись используя значение поля `SECRET_KEY`

[Шаг 6](./step-06.md)
