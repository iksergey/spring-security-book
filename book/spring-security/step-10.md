## Добавление пользователей

Обновим основной класс приложения, для автоматического создания пользователей

Класс `ContactsAppApplication` является точкой входа в Spring Boot приложение и реализует интерфейс `CommandLineRunner`, что позволяет выполнить код при запуске приложения.

Основные функции класса:

1. **Инициализация приложения:**
   - Метод `main` запускает Spring Boot приложение.

2. **Создание начальных пользователей:**
   - Метод `run` вызывается автоматически при старте и создает двух пользователей: администратора и обычного пользователя.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ru.ksergey.ContactsApp.enums.AppRole;
import ru.ksergey.ContactsApp.model.AppUser;
import ru.ksergey.ContactsApp.repository.AppUserRepository;

@SpringBootApplication
public class ContactsAppApplication implements CommandLineRunner {

    @Autowired
    private AppUserRepository appUserRepository;

    public static void main(String[] args) {
        SpringApplication.run(ContactsAppApplication.class, args);
    }

    @Override
    public void run(String... args) {
        createUserIfNotExists("admin@admin.admin", AppRole.ADMIN);
        createUserIfNotExists("user@user.user", AppRole.USER);
    }

    private void createUserIfNotExists(String email, AppRole role) {
        if (appUserRepository.findByEmail(email).isEmpty()) {
            AppUser account = new AppUser();
            account.setEmail(email);
            account.setPassword(new BCryptPasswordEncoder().encode("pass"));
            account.setRole(role);
            appUserRepository.save(account);
        }
    }
}
```

В результате запроса на `http://localhost:PORT/api/auth/login` с моделью запроса:

```json
{
  "email": "string",
  "password": "string"
}
```
Ожидаем получить `200 OK` с моделью ответа:

```json
{
  "email": "string",
  "jwtToken": "ey1NiJ9.eyJzdWIiTAzMjYsNTgxMTc2Nn0.NVfYiKGQP-MSw",
  "refreshToken": "eiJ9.eyJzdWIU4MTAzMjYsImV4cyNT.vb5qeAopc1oaF"
}
```


[Шаг 11](./step-11.md)
