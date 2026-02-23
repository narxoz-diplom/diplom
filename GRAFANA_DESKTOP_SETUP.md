# Настройка Grafana Desktop для визуализации метрик Prometheus

## Шаг 1: Подключение к Prometheus

1. Откройте Grafana Desktop
2. Перейдите в **Configuration** → **Data Sources** (или нажмите `+` → **Add data source**)
3. Выберите **Prometheus**
4. Заполните настройки:
   - **Name**: `Prometheus` (или любое другое имя)
   - **URL**: `http://localhost:9090`
   - **Access**: `Server (default)`
5. Нажмите **Save & Test** - должно появиться сообщение "Data source is working"

## Шаг 2: Создание первого дашборда

### 2.1 Создание нового дашборда

1. Нажмите **+** → **Create** → **Dashboard**
2. Нажмите **Add visualization** (или **Add panel**)

### 2.2 Основные метрики для мониторинга

#### Метрики JVM (Java Virtual Machine)

**Использование памяти:**
```
jvm_memory_used_bytes{application="file-service"}
```

**Размер heap:**
```
jvm_memory_max_bytes{application="file-service", area="heap"}
```

**Количество потоков:**
```
jvm_threads_live_threads{application="file-service"}
```

#### Метрики HTTP запросов

**Количество HTTP запросов:**
```
http_server_requests_seconds_count{application="api-gateway"}
```

**Время ответа HTTP:**
```
http_server_requests_seconds_sum{application="api-gateway"} / http_server_requests_seconds_count{application="api-gateway"}
```

**HTTP запросы по статусам:**
```
http_server_requests_seconds_count{application="api-gateway", status="200"}
http_server_requests_seconds_count{application="api-gateway", status="404"}
http_server_requests_seconds_count{application="api-gateway", status="500"}
```

#### Метрики базы данных

**Активные соединения:**
```
hikari_connections_active{application="file-service"}
```

**Ожидающие соединения:**
```
hikari_connections_idle{application="file-service"}
```

#### Метрики RabbitMQ

**Сообщения отправлено:**
```
rabbitmq_published_total{application="notification-service"}
```

**Сообщения получено:**
```
rabbitmq_consumed_total{application="notification-service"}
```

## Шаг 3: Примеры готовых панелей

### Панель 1: CPU и память сервисов

**Query:**
```promql
jvm_memory_used_bytes{application=~"file-service|course-service|api-gateway|auth-service|notification-service"}
```

**Visualization**: Graph или Time series
**Legend**: `{{application}} - {{area}}`

### Панель 2: HTTP запросы в секунду

**Query:**
```promql
rate(http_server_requests_seconds_count{application="api-gateway"}[1m])
```

**Visualization**: Graph
**Unit**: `ops/sec`

### Панель 3: Время ответа (p50, p95, p99)

**Query для p50:**
```promql
histogram_quantile(0.50, rate(http_server_requests_seconds_bucket{application="api-gateway"}[5m]))
```

**Query для p95:**
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{application="api-gateway"}[5m]))
```

**Query для p99:**
```promql
histogram_quantile(0.99, rate(http_server_requests_seconds_bucket{application="api-gateway"}[5m]))
```

**Visualization**: Graph
**Unit**: `s` (секунды)

### Панель 4: Ошибки HTTP

**Query:**
```promql
sum(rate(http_server_requests_seconds_count{application="api-gateway", status=~"5.."}[1m])) by (status)
```

**Visualization**: Graph
**Legend**: `{{status}}`

### Панель 5: Активные соединения к БД

**Query:**
```promql
hikari_connections_active{application=~".*"}
```

**Visualization**: Stat или Gauge
**Unit**: `short`

### Панель 6: Сообщения RabbitMQ

**Query (отправлено):**
```promql
rate(rabbitmq_published_total{application="notification-service"}[1m])
```

**Query (получено):**
```promql
rate(rabbitmq_consumed_total{application="notification-service"}[1m])
```

**Visualization**: Graph
**Unit**: `ops/sec`

## Шаг 4: Полезные запросы для отладки

### Проверка доступности всех сервисов

```promql
up{job=~"file-service|course-service|api-gateway|auth-service|notification-service"}
```

### Общее количество запросов по всем сервисам

```promql
sum(rate(http_server_requests_seconds_count[1m])) by (application)
```

### Топ 10 самых медленных эндпоинтов

```promql
topk(10, histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m])))
```

## Шаг 5: Экспорт/импорт дашбордов

После создания дашборда вы можете:
1. Сохранить его (кнопка **Save**)
2. Экспортировать в JSON (кнопка **Share** → **Export**)
3. Импортировать готовые дашборды из Grafana.com

## Полезные ссылки

- [Grafana Prometheus Data Source](https://grafana.com/docs/grafana/latest/datasources/prometheus/)
- [PromQL Query Examples](https://prometheus.io/docs/prometheus/latest/querying/examples/)
- [Grafana Dashboard Library](https://grafana.com/grafana/dashboards/)


