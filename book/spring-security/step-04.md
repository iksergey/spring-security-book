# Обновление пользователя

Расширим пользователя AppUser интерфейсом `UserDetails` из `org.springframework.security.core.userdetails`

Основные особенности класса:
- Аннотирован как сущность JPA для хранения в базе данных
- Содержит поля для хранения информации о пользователе (`id`, `имя`, `email`, `пароль`, `роль`)
- Реализует методы `UserDetails` для интеграции со Spring Security
- Использует `AppRole` для определения роли пользователя
- Метод `getAuthorities()` возвращает список прав доступа на основе роли пользователя

Этот класс является центральным элементом в системе управления пользователями, объединяя функциональность хранения данных и аутентификации. 

`AppUser` реализует интерфейс `UserDetails` из Spring Security. Это позволяет использовать данный класс для аутентификации и авторизации в системе безопасности Spring.

```java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.ksergey.ContactsApp.enums.AppRole;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "app_user")
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;
    private String email;
    private String password;
    private AppRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

### Назначение UserDetails

UserDetails - это интерфейс в Spring Security, который предоставляет основную информацию о пользователе. Он служит мостом между вашей пользовательской моделью данных и внутренними механизмами Spring Security.

### Основные функции

1. **Аутентификация**: UserDetails содержит информацию, необходимую для аутентификации пользователя, такую как имя пользователя и пароль.

2. **Авторизация**: Интерфейс предоставляет методы для получения ролей и прав доступа пользователя, что используется при авторизации.

3. **Управление состоянием аккаунта**: UserDetails содержит методы для проверки состояния аккаунта (активен, заблокирован, истек срок действия и т.д.).

### getAuthorities()

    Этот метод возвращает коллекцию прав (ролей) пользователя. В вашем случае он возвращает список с одним элементом - ролью пользователя, преобразованной в SimpleGrantedAuthority. Это используется Spring Security для определения, к каким ресурсам пользователь имеет доступ.

### getUsername()

    Возвращает уникальный идентификатор пользователя. В вашей реализации в качестве имени пользователя используется email. Spring Security использует это значение для идентификации пользователя в системе.

### isAccountNonExpired()

    Показывает, не истек ли срок действия аккаунта пользователя. Возвращая true, вы указываете, что срок действия аккаунта никогда не истекает. Если бы вы хотели реализовать функционал истечения срока действия аккаунтов, вы могли бы изменить эту логику.

### isAccountNonLocked()

    Определяет, не заблокирован ли аккаунт пользователя. Возвращая true, вы указываете, что аккаунт никогда не блокируется. Это можно использовать для реализации временной блокировки пользователей, например, после нескольких неудачных попыток входа.

### isCredentialsNonExpired()

    Показывает, не истек ли срок действия учетных данных (обычно пароля) пользователя. Возвращая true, вы указываете, что срок действия учетных данных никогда не истекает. Это может быть полезно, если вы хотите реализовать политику регулярной смены паролей.

### isEnabled()

    Определяет, активен ли аккаунт пользователя. Возвращая true, вы указываете, что аккаунт всегда активен. Этот метод можно использовать для реализации функционала деактивации аккаунтов, например, при удалении пользователя или при необходимости подтверждения email.

### Замечания:

1. В текущей реализации все методы, кроме `getAuthorities()` и `getUsername()`, всегда возвращают true. Это означает, что все аккаунты всегда активны, не заблокированы и не имеют срока действия.

2. Для более гибкой системы безопасности вы можете добавить соответствующие поля в вашу сущность `AppUser` (например, `boolean isEnabled`, `Date expirationDate` и т.д.) и изменить реализацию этих методов, чтобы они возвращали актуальное состояние.

3. Реализация интерфейса `UserDetails` позволяет Spring Security использовать ваш класс `AppUser` непосредственно в процессе аутентификации и авторизации, что упрощает интеграцию с фреймворком безопасности.

## UserDetails

это ключевой интерфейс в Spring Security, который играет важную роль в процессе аутентификации и авторизации пользователей.

### Основное назначение UserDetails

1. **Представление пользователя**: `UserDetails` служит в качестве контейнера для основной информации о пользователе, необходимой для аутентификации и авторизации.

2. **Адаптер между БД и Spring Security**: `UserDetails` можно рассматривать как адаптер между вашей базой данных пользователей и тем, что требуется Spring Security внутри `SecurityContextHolder`.

3. **Стандартизация информации о пользователе**: Интерфейс определяет стандартный набор методов для получения критически важной информации о пользователе, такой как имя пользователя, пароль, роли и статус аккаунта.

### Ключевые функции UserDetails

1. **Предоставление учетных данных**: Методы getUsername() и getPassword() предоставляют базовую информацию для аутентификации.

2. **Управление ролями и правами**: Метод getAuthorities() возвращает коллекцию прав (ролей) пользователя, что критически важно для авторизации.

3. **Контроль состояния аккаунта**: Методы isAccountNonExpired(), isAccountNonLocked(), isCredentialsNonExpired() и isEnabled() позволяют управлять различными аспектами состояния учетной записи пользователя.

### Вопрос

Провалитесь в интерфейс `UserDetails` внимательно изучите его. Какие члены этого интерфейса не реализованы в `AppUser`. Почему? [Ответ](./step-04-answer.md) 

[Про JWT \>\>](./step-05-about-jwt.md)
