## Шаг 1. Сборка Docker-образа

1. Сохраните ваш Java-файл как `Program.java` в той же директории, где находится Dockerfile.

```java
public class Program {

    public static void main(String[] args) {
        System.out.println("Hello java");
    }
}
```

Простой Dockerfile, который собирает docker-образ на основе Java-файла:

```dockerfile
FROM openjdk:17
COPY Program.java /app/
WORKDIR /app
RUN javac Program.java
CMD ["java", "Program"]
```

2. Выполните команду для сборки образа:
   ```
   docker build -t my-java-app .
   ```

3. Убедитесь, что образ собран корректно. Запустите контейнер с помощью команды:
   ```
   docker run my-java-app
   ```

Это должно вывести "Hello java" в консоль.

## Шаг 2. Настройка GitHub Actions workflow

1. В настройках вашего GitHub репозитория (Settings -> Secrets and variables -> Actions) добавьте два секрета:
   - `DOCKERHUB_USERNAME`: ваше имя пользователя в Docker Hub
   - `DOCKERHUB_PASSWORD`: ваш пароль доступа Docker Hub

2. Убедитесь, что ваш Dockerfile находится в корневой директории репозитория.

3. Создайте файл `.github/workflows/docker-build-push.yml` в вашем репозитории и вставьте в него код.

Yaml-файл, который будет собирать Docker-образ Java-приложения и отправлять его в Docker Hub.

```yaml
name: Build and Push Docker Image

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build-and-push:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout repository
      uses: actions/checkout@v3

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v2

    - name: Login to Docker Hub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKERHUB_USERNAME }}
        password: ${{ secrets.DOCKERHUB_TOKEN }}

    - name: Build and push Docker image
      uses: docker/build-push-action@v4
      with:
        context: .
        push: true
        tags: ${{ secrets.DOCKERHUB_USERNAME }}/my-java-app:latest
```

4. При следующем push в ветку `main` GitHub Actions автоматически запустит этот workflow.

Обратите внимание, что в этом примере образ тегируется как `latest`. В реальных проектах рекомендуется использовать более специфичные теги, например, номера версий или хеши коммитов.
