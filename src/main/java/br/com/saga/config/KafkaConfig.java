package br.com.saga.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    public static final String ORDER_CREATED_TOPIC = "order-created";
    public static final String PAYMENT_PROCESSED_TOPIC = "payment-processed";
    public static final String ORDER_COMPENSATE_TOPIC = "order-compensate";
    public static final String SELLER_NOTIFIED_TOPIC = "seller-notified";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(props);
        factory.setValueSerializer(new JsonSerializer<>(mapper));
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(ORDER_CREATED_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentProcessedTopic() {
        return TopicBuilder.name(PAYMENT_PROCESSED_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic orderCompensateTopic() {
        return TopicBuilder.name(ORDER_COMPENSATE_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic sellerNotifiedTopic() {
        return TopicBuilder.name(SELLER_NOTIFIED_TOPIC).partitions(3).replicas(1).build();
    }
}
