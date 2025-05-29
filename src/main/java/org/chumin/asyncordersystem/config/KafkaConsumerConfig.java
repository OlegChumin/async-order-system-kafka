package org.chumin.asyncordersystem.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.chumin.asyncordersystem.orderservice.model.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурационный класс KafkaConsumerConfig настраивает компоненты для подключения Kafka consumer'ов
 * в приложении на Spring Boot.
 *
 * <p>Основные задачи:</p>
 * <ul>
 *     <li>Настройка десериализации ключей и значений Kafka-сообщений</li>
 *     <li>Указание адреса Kafka-брокера (bootstrap servers)</li>
 *     <li>Создание контейнеров для {@link org.springframework.kafka.annotation.KafkaListener}-ов</li>
 * </ul>
 *
 * <p>Класс аннотирован {@link org.springframework.kafka.annotation.EnableKafka},
 * что активирует поддержку KafkaListener'ов в контексте Spring.</p>
 *
 * <p>Использует шаблон проектирования «Фабрика» (Factory Pattern)
 * для централизованной и переиспользуемой конфигурации консьюмеров.</p>
 *
 * <p>Фабрики позволяют:</p>
 * <ul>
 *     <li>создавать Kafka-консьюмеров с нужными параметрами (серверы, десериализация, безопасность и т.д.);</li>
 *     <li>легко переиспользовать конфигурацию в нескольких компонентах;</li>
 *     <li>разделять ответственность и облегчать тестирование.</li>
 * </ul>
 */

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    /**
     * Создаёт {@link ConsumerFactory}, который будет использоваться KafkaListener'ами для чтения сообщений.
     * <p>
     * Фабрика инкапсулирует конфигурацию консьюмера: bootstrap-серверы, десериализаторы, список доверенных пакетов и пр.
     * Это позволяет:
     * <ul>
     *   <li>гибко управлять параметрами Kafka-консьюмера в одном месте;</li>
     *   <li>создавать разные фабрики под разные группы или топики, если нужно;</li>
     *   <li>избегать дублирования кода и упростить поддержку;</li>
     *   <li>тестировать консьюмеры независимо, подставляя mock-фабрики.</li>
     * </ul>
     *
     * @return готовая фабрика Kafka-консьюмеров с базовой конфигурацией.
     */
    @Bean
    public ConsumerFactory<String, Order> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Значение будет подхвачено из application.yml
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Создаёт {@link ConcurrentKafkaListenerContainerFactory} — фабрику контейнеров для KafkaListener'ов.
     * <p>
     * Каждый KafkaListener работает внутри контейнера, обеспечивающего:
     * <ul>
     *   <li>подключение к Kafka через {@link ConsumerFactory};</li>
     *   <li>параллельную обработку сообщений (через concurrency);</li>
     *   <li>стратегии ack/commit, error handling и др.</li>
     * </ul>
     * Контейнер-фабрика связывается с нашей ConsumerFactory.
     *
     * @return listener-контейнер с поддержкой многопоточной обработки сообщений.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Order> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Order> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}