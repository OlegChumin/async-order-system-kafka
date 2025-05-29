package org.chumin.asyncordersystem.orderservice.restcontroller;

import org.chumin.asyncordersystem.orderservice.kafka.OrderProducer;
import org.chumin.asyncordersystem.orderservice.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        orderProducer.sendOrder(order);
        return ResponseEntity.ok("Order sent to Kafka");
    }
}
