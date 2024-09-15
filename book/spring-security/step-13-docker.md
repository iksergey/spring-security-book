## Упаковка приложения в Docker-образ

1. Скачайте и установите Docker с официального сайта: http://docker.com

2. После установки откройте терминал и проверьте корректность установки, выполнив команду:

   ```bash
   docker --version
   ```

   В ответ вы должны увидеть что-то вроде:
   ```bash
   Docker version 27.2.0, build 3ab4256
   ```

## Подготовка проекта

1. В корневой папке вашего Spring-проекта создайте файл с названием `dockerfile` (без расширения).

2. Скопируйте следующее содержимое в `dockerfile`:

   ```yaml
   FROM eclipse-temurin:22-jdk-alpine as build
   WORKDIR /workspace/app

   COPY mvnw .
   COPY .mvn .mvn
   COPY pom.xml .
   COPY src src

   RUN ./mvnw install -DskipTests
   RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

   FROM eclipse-temurin:22-jre-alpine
   VOLUME /tmp
   ARG DEPENDENCY=/workspace/app/target/dependency
   COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
   COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
   COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

   ENV SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:4444/contacts_db
   ENV SPRING_DATASOURCE_USERNAME=root
   ENV SPRING_DATASOURCE_PASSWORD=12345678

   ENTRYPOINT ["java","-cp","app:app/lib/*","ru.ksergey.ContactsApp.ContactsAppApplication"]
   ```
При необходимости внесите изменения в соответствии с вашим окружением

## Сборка Docker-образа

1. Откройте терминал и перейдите в корневую папку вашего проекта.

2. Выполните следующую команду для сборки Docker-образа:

   ```bash
   docker build -t spring-security-book . --no-cache
   ```

   Эта команда создаст образ с именем "spring-security-book".

## Запуск контейнера

1. После успешной сборки образа запустите контейнер с помощью команды:

   ```bash
   docker run -d -p 8081:8080 spring-security-book
   ```

   Эта команда запустит контейнер в фоновом режиме и свяжет порт 8080 контейнера с портом 8081 вашего компьютера.

## Проверка работы приложения

1. Откройте веб-браузер и перейдите по адресу `http://localhost:8081/swagger-ui/index.html#/` (или другому адресу, если вы изменили порт).

2. Если всё настроено правильно, вы должны увидеть ваше Spring-приложение в работе.

**Важные замечания:**

- Убедитесь, что у вас есть доступ к MySQL-серверу по указанному в `dockerfile` адресу и с указанными учетными данными.
- Если вы используете другую базу данных или другие настройки, измените соответствующие переменные окружения в `dockerfile`.
- При возникновении проблем проверьте логи контейнера с помощью команды `docker logs <ID_контейнера>`.

[Попробовать что-то большее \>\> ](./step-13-docker-compose.md)
