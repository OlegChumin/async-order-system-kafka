package org.chumin.asyncordersystem.orderservice.kafka;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.chumin.asyncordersystem.orderservice.model.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderProducer {
    private final KafkaTemplate<String, Order> kafkaTemplate;

    @PostConstruct
    public void init() {
        log.info("KafkaTemplate is using: {}", kafkaTemplate);
    }


    public OrderProducer(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(Order order) {
        kafkaTemplate.send("orders", order.getId(), order); // order.getId() <- MurmurHash (от MUltiple Results MURmur)
    }
}
