# Опишем пользователя системы

## Добавим перечень ролей

В пакет `enums` добавим перечисление

```java
public enum AppRole {
    ADMIN,
    USER
}
```

В пакет `model` добавим сущность `AppUser`, которая представляет собой пользователя в приложении.

```java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import ru.ksergey.ContactsApp.enums.AppRole;

@Data
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;
    private String email;
    private String password;
    private AppRole role;
}
```

В пакет `repository` добавим `AppUserRepository`

```java
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.ksergey.ContactsApp.model.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByEmail(String email);
}
```

[Шаг 4](./step-04.md)
