# Async Order System via Kafka

Микросервисное Spring Boot приложение для демонстрации асинхронной обработки заказов через Kafka.

## 📦 Архитектура

Система состоит из трёх компонентов:

- **OrderService** — приём заказов от клиентов через REST API и отправка их в Kafka.
- **BillingService** — Kafka consumer, обрабатывающий заказы и эмулирующий списание средств.
- **NotificationService** — Kafka consumer, уведомляющий клиента (логированием SMS/email) о статусе заказа.

## ⚙️ Технологии

- Java 17
- Spring Boot 3.5.x
- Spring Kafka
- Apache Kafka (через Docker Compose)
- Kafka UI для отладки (проект `provectuslabs/kafka-ui`)
- Postman (коллекция для вызова API)

## 🚀 Быстрый старт

1. Убедись, что установлен Docker и Docker Compose.
2. Собери и запусти приложение:

## Kafka UI

Для удобного просмотра очередей и сообщений используется [Kafka UI](https://github.com/provectus/kafka-ui), который автоматически поднимается через `docker-compose`.

После запуска проекта Kafka UI будет доступен по адресу:

🧭 http://localhost:8088/

На главной странице UI можно:
- просматривать топики и партиции (`orders`, в нашем случае),
- следить за количеством сообщений,
- читать и декодировать сообщения в реальном времени.

Контейнер конфигурируется через `compose.yaml` и подключён к брокеру Kafka.
