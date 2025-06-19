package org.chumin.asyncordersystem.billingservice;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.chumin.asyncordersystem.orderservice.model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Сервис обработки заказов для биллинга.
 * <p>
 * Получает сообщения из Kafka-топика "orders" и имитирует списание средств с пользователя
 * на основе типа продукта и количества.
 */
@Slf4j
@Service
public class BillingService {
    @PostConstruct
    public void init() {
        log.info("[BillingService] Kafka listener initialized and ready.");
    }


    /**
     * Обрабатывает входящее Kafka-сообщение с заказом.
     * Вызывается автоматически KafkaListener'ом при поступлении сообщения в топик "orders".
     *
     * @param orderRecord сообщение Kafka, содержащее заказ
     */
    @KafkaListener(topics = "orders", groupId = "billing-group")
    public void handleOrder(ConsumerRecord<String, Order> orderRecord, Acknowledgment ack) {
        Order order = orderRecord.value();
        log.info("[BillingService] Получен заказ: id={}, product={}, quantity={}",
                order.getId(), order.getProduct(), order.getQuantity());
        /*
            TODO:  if (notificationAlreadySent(order.getId())) {
            log.info("⚠️ Уведомление по заказу id={} уже отправлено, пропускаем", order.getId());
            ack.acknowledge();
            return; } -> это через БД или через REDIS
 💸         Эмуляция логики списания денег
        */

        double unitPrice = getPrice(order.getProduct());
        double totalAmount = unitPrice * order.getQuantity();

        log.info("[BillingService] Списано {:.2f} ₽ за заказ id={} ({} x {:.2f})",
                totalAmount, order.getId(), order.getQuantity(), unitPrice);
        ack.acknowledge(); // только теперь Kafka зафиксирует offset
    }

    /**
     * Возвращает цену за единицу товара.
     * <p>
     * Используется для имитации подсчёта стоимости заказа.
     *
     * @param product название продукта
     * @return цена за единицу
     */
    private double getPrice(String product) {
        return switch (product.toLowerCase()) {
            case "laptop" -> 75000;
            case "phone" -> 40000;
            case "tablet" -> 30000;
            default -> 10000;
        };
    }
}
