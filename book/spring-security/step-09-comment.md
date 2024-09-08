### Замечание

Сейчас, эндпоинт `/register` возвращает модель `AppUser`

```json
{
  "id": 1,
  "fullName": "string",
  "email": "string",
  "password": "$2a$10$U1nJmJ68G/.MGxatxutNT.70WcGJrhdYiode6vAALPtpcGtJq9qSm",
  "role": "USER",
  "enabled": true,
  "credentialsNonExpired": true,
  "authorities": [
    {
      "authority": "USER"
    }
  ],
  "username": "string",
  "accountNonExpired": true,
  "accountNonLocked": true
}
```

Очевидно, быть так не должно. Данный эндпоинт описан исключительно в целях демонстрации.

Для более правильной версии, можно использовать dto-модель

```java
public class RegisterResponseDto
{
    private String fullName;
    private String email;
    private String jwtToken;
}
```

Пока что это делать не будем

[\<\< Назад](./step-09.md)
