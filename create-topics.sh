#!/bin/bash

# 🔄 Ждём, пока Kafka станет доступной
# cub — утилита от Confluent, встроена в образ
# Важно! Указываем внутренний адрес и порт: kafka:29092
cub kafka-ready -b kafka:29092 1 20

# 🧱 Создаём топик 'orders'
kafka-topics \
  --bootstrap-server kafka:29092 \
  --create \
  --if-not-exists \
  --topic orders \
  --partitions 3 \
  --replication-factor 1 || echo "❌ Не удалось создать топик, возможно он уже существует или Kafka недоступна."

# ✅ Готово!
echo "Топик 'orders' создан или уже существует."

# 📤 Завершаем скрипт, чтобы контейнер завершился
exit 0
