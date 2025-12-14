# Микросервисная система управления файлами и уведомлениями

Распределенная микросервисная система для обработки и управления данными с высокой доступностью, гибкостью и масштабируемостью.

## Архитектура системы

### Компоненты

1. **API Gateway** (порт 8083) - Фасад для маршрутизации запросов между клиентами и микросервисами
2. **File Service** (порт 8081) - Главный сервис для работы с файлами
3. **Notification Service** (порт 8082) - Сервис уведомлений
4. **Keycloak** (порт 8080) - Аутентификация и авторизация
5. **MinIO** (порты 9000, 9001) - Хранилище файлов
6. **RabbitMQ** (порты 5672, 15672) - Брокер сообщений
7. **Redis** (порт 6379) - Кеширование
8. **PostgreSQL** (порт 5432) - База данных
9. **Prometheus** (порт 9090) - Мониторинг метрик
10. **Grafana** (порт 3000) - Визуализация метрик
11. **Frontend** (порт 3001) - React приложение

## Требования

- Docker и Docker Compose
- Maven 3.6+
- Java 17+
- Node.js 18+ (для локальной разработки frontend)

## Быстрый старт

### 1. Клонирование и подготовка

```bash
cd "Course tanat"
```

### 2. Настройка Keycloak

Перед запуском необходимо настроить Keycloak:

1. Запустите только инфраструктуру:
```bash
docker-compose up -d postgres keycloak
```

2. Дождитесь запуска Keycloak (около 30-60 секунд), затем откройте:
   - http://localhost:8080
   - Логин: `admin`
   - Пароль: `admin`

3. Создайте Realm:
   - Нажмите на выпадающий список в левом верхнем углу
   - Выберите "Create Realm"
   - Название: `microservices-realm`
   - Нажмите "Create"

4. Создайте Client:
   - В меню слева выберите "Clients"
   - Нажмите "Create client"
   - Client ID: `microservices-client`
   - Client authentication: OFF
   - Authorization: OFF
   - Нажмите "Next"
   - Включите "Standard flow" и "Direct access grants"
   - Valid redirect URIs: `http://localhost:3001/*`
   - Web origins: `*`
   - Нажмите "Save"

5. Создайте пользователя:
   - В меню слева выберите "Users"
   - Нажмите "Add user"
   - Username: `testuser`
   - Email: `test@example.com`
   - Email verified: ON
   - Нажмите "Create"
   - Перейдите на вкладку "Credentials"
   - Password: `test123`
   - Temporary: OFF
   - Нажмите "Set password"

### 3. Запуск всей системы

```bash
docker-compose up -d
```

### 4. Проверка работы

- Frontend: http://localhost:3001
- Keycloak Admin: http://localhost:8080
- RabbitMQ Management: http://localhost:15672 (admin/admin)
- MinIO Console: http://localhost:9001 (minioadmin/minioadmin)
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)

## Разработка

### Локальная разработка сервисов

Для разработки отдельных сервисов без Docker:

```bash
# File Service
cd file-service
mvn spring-boot:run

# Notification Service
cd notification-service
mvn spring-boot:run

# API Gateway
cd api-gateway
mvn spring-boot:run
```

### Локальная разработка Frontend

```bash
cd frontend
npm install
npm run dev
```

## API Endpoints

### File Service

- `POST /api/files/upload` - Загрузка файла
- `GET /api/files` - Список файлов пользователя
- `GET /api/files/{id}` - Получить информацию о файле
- `GET /api/files/{id}/download` - Скачать файл
- `DELETE /api/files/{id}` - Удалить файл

### Notification Service

- `GET /api/notifications` - Список всех уведомлений
- `GET /api/notifications/unread` - Непрочитанные уведомления
- `GET /api/notifications/unread/count` - Количество непрочитанных
- `PUT /api/notifications/{id}/read` - Отметить как прочитанное
- `PUT /api/notifications/read-all` - Отметить все как прочитанные

## Структура проекта

```
.
├── api-gateway/          # API Gateway сервис
├── file-service/         # Сервис управления файлами
├── notification-service/ # Сервис уведомлений
├── frontend/             # React приложение
├── monitoring/           # Конфигурация Prometheus
├── docker-compose.yml    # Docker Compose конфигурация
└── README.md            # Документация
```

## Особенности

- ✅ Аутентификация через Keycloak
- ✅ Система ролей (Admin, Teacher, Client) с разграничением доступа
- ✅ Загрузка и управление файлами через MinIO
- ✅ Асинхронная обработка через RabbitMQ
- ✅ Кеширование через Redis
- ✅ Мониторинг через Prometheus и Grafana
- ✅ RESTful API
- ✅ React SPA с современным UI

## Система ролей

Система поддерживает три роли с разными правами доступа:

- **Admin (Администратор)**: Полный доступ - может загружать, удалять и просматривать все файлы
- **Teacher (Учитель)**: Может загружать файлы (включая видео) и просматривать свои файлы
- **Client (Клиент)**: Только просмотр - может только просматривать и скачивать свои файлы

Подробная инструкция по настройке ролей в Keycloak: [KEYCLOAK_ROLES_SETUP.md](KEYCLOAK_ROLES_SETUP.md)

## Остановка системы

```bash
docker-compose down
```

Для удаления всех данных (volumes):

```bash
docker-compose down -v
```

## Устранение неполадок

### Keycloak не запускается

Убедитесь, что PostgreSQL запущен и здоров:
```bash
docker-compose ps
docker-compose logs keycloak
```

### Сервисы не могут подключиться к базе данных

Проверьте, что все сервисы находятся в одной Docker сети:
```bash
docker network ls
docker network inspect course-tanat_microservices-network
```

### Проблемы с аутентификацией

1. Проверьте настройки Keycloak realm и client
2. Убедитесь, что redirect URI правильный
3. Проверьте токены в браузере (DevTools -> Application -> Local Storage)

## Лицензия

Этот проект создан в образовательных целях.

