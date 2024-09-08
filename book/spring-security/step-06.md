## Опишем AppUserService

В пакет `service.jwt` добавим класс `AppUserService`

Класс `AppUserService` предоставляет реализацию `UserDetailsService` для аутентификации пользователей в Spring Security. Он загружает данные пользователя из базы данных по email и возвращает объект UserDetails, необходимый для проверки учетных данных и авторизации.

Класс использует `AppUserRepository` для поиска пользователя в базе данных и преобразует найденного `AppUser` в `UserDetails`. 

Если пользователь не найден, выбрасывается исключение `UsernameNotFoundException`. 

Метод `getDetailsService()` возвращает анонимную реализацию `UserDetailsService`, которую можно использовать в конфигурации Spring Security.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.ksergey.ContactsApp.model.AppUser;
import ru.ksergey.ContactsApp.repository.AppUserRepository;

@Service
@RequiredArgsConstructor
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    public UserDetailsService getDetailsService() {

        UserDetailsService detailsService = new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                AppUser user = appUserRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
                return user;
            }
        };

        return detailsService;
    }
}
```

[Шаг 7](./step-07.md)
