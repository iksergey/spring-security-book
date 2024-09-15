## Docker Compose

1. Откройте терминал и выполните одну из следующих команд:

   ```bash
   docker-compose version
   ```
   или
   ```bash
   docker compose version
   ```

2. Вы должны увидеть ответ, похожий на:
   ```bash
   Docker Compose version v2.29.2-desktop.2
   ```

   Если команда не распознана, вам необходимо установить Docker Compose.

## Подготовка файлов

1. Остановите и удалите ранее созданный контейнер, если он еще запущен.

2. В корневой папке вашего проекта создайте файл `compose.yaml`.

3. Скопируйте следующее содержимое в `compose.yaml`:

   ```yaml
   version: '3.8'

   services:
     mysql:
       image: mysql:8.0
       container_name: mysql_db
       environment:
         MYSQL_ROOT_PASSWORD: 12345678
         MYSQL_DATABASE: contacts_db
       ports:
         - "4444:3306"
       volumes:
         - mysql_data:/var/lib/mysql
       healthcheck:
         test: [ "CMD", "mysqladmin", "ping", "-h", "localhost" ]
         timeout: 20s
         retries: 10

     app:
       build: .
       container_name: contacts_app
       ports:
         - "8080:8080"
       environment:
         SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/contacts_db
         SPRING_DATASOURCE_USERNAME: root
         SPRING_DATASOURCE_PASSWORD: 12345678
       depends_on:
         mysql:
           condition: service_healthy

   volumes:
     mysql_data:
   ```

При необходимости внесите изменения в соответствии с вашим окружением

## Запуск приложения

1. Откройте терминал и перейдите в папку с файлом `compose.yaml`.

2. Выполните одну из следующих команд для сборки и запуска контейнеров:

   ```bash
   docker-compose up --build
   ```
   или
   ```bash
   docker compose up --build
   ```

3. Дождитесь завершения процесса. Docker Compose выполнит следующие действия:
   - Создаст и запустит контейнер с MySQL.
   - Соберет ваше Java-приложение.
   - Запустит контейнер с вашим приложением после успешного запуска MySQL.

4. После успешного запуска, ваше приложение должно быть доступно по адресу `http://localhost:8080/...`.

## Устранение неполадок

Если приложение недоступно по адресу `localhost:8080/...`, выполните следующие шаги:

1. Проверьте логи контейнеров на наличие ошибок:
   ```bash
   docker-compose logs
   ```

2. Убедитесь, что оба контейнера (MySQL и ваше приложение) запущены:
   ```bash
   docker-compose ps
   ```

3. Проверьте, что порт 8080 не занят другим процессом на вашем компьютере.

4. Если проблема связана с подключением к базе данных, убедитесь, что параметры подключения в вашем приложении соответствуют указанным в `compose.yaml`.

5. После устранения ошибок перезапустите контейнеры:
   ```bash
   docker-compose down
   docker-compose up --build
   ```

\* Для запуска в фоновом режиме используйте 
   
   ```bash
   docker-compose -d up
   ```

Следуя этой инструкции, вы должны успешно запустить ваше Java-приложение вместе с MySQL в Docker-контейнерах.
