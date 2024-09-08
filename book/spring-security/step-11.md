# Финал

Добавим `AdminController`

```java
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    @GetMapping
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("Ok");
    }
}
```

Добавим `UserController`

```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    @GetMapping
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("Ok");
    }
}
```

Тестируем запросы

- Запрос на `http://localhost:PORT/api/user` с токеном роли USER должен давать ответ `ok`
- Запрос на `http://localhost:PORT/api/user` с токеном роли ADMIN должен давать ответ `Error: response status is 401`

- Запрос на `http://localhost:PORT/api/admin` с токеном роли ADMIN должен давать ответ `ok`
- Запрос на `http://localhost:PORT/api/admin` с токеном роли USER должен давать ответ `Error: response status is 401`

[Далее](./step-01-p1.md)
