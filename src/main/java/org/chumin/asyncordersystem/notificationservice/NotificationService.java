package org.chumin.asyncordersystem.notificationservice;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.chumin.asyncordersystem.orderservice.model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Сервис, отвечающий за отправку уведомлений клиенту по заказу.
 * Подписан на Kafka-топик {@code orders}, обрабатывает входящие сообщения.
 */
@Slf4j
@Service
public class NotificationService {

    @PostConstruct
    public void init() {
        log.info("[NotificationService] Kafka listener initialized and ready.");
    }


    /**
     * Kafka listener, обрабатывающий новые заказы.
     *
     * @param orderRecord Kafka-сообщение с заказом.
     */
    @KafkaListener(topics = "orders", groupId = "notification-group")
    public void handleOrder(ConsumerRecord<String, Order> orderRecord, Acknowledgment ack) {
        Order order = orderRecord.value();
        log.info("[NotificationService] Получен заказ: {}", order);
    /*
    TODO:
            if (notificationAlreadySent(order.getId())) {
                log.info("⚠️ Уведомление по заказу id={} уже отправлено, пропускаем", order.getId());
                ack.acknowledge();
                return;
            } -> это через БД или через REDIS
    */


        sendSmsNotification(order);
        sendEmailNotification(order);

        ack.acknowledge(); // ✅ ручной коммит offset'а
    }

    private void sendSmsNotification(Order order) {
        log.info("[NotificationService] ✅ SMS отправлено клиенту: 'Заказ #{} ({}), количество: {}'",
                order.getId(), order.getProduct(), order.getQuantity());
    }

    private void sendEmailNotification(Order order) {
        log.info("[NotificationService] ✅ Email отправлен: 'Ваш заказ #{} ({} x {}) подтверждён'",
                order.getId(), order.getProduct(), order.getQuantity());
    }
}
