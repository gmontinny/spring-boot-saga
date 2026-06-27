package br.com.saga.saga;

import br.com.saga.config.KafkaConfig;
import br.com.saga.domain.enums.SagaStatus;
import br.com.saga.repository.OrderItemRepository;
import br.com.saga.repository.OrderPaymentRepository;
import br.com.saga.repository.OrderRepository;
import br.com.saga.repository.SagaEventRepository;
import br.com.saga.domain.entity.SagaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaEventListener {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final SagaEventRepository sagaEventRepository;

    @KafkaListener(topics = KafkaConfig.ORDER_COMPENSATE_TOPIC, groupId = "saga-group")
    @Transactional
    public void handleCompensation(String sagaId) {
        log.warn("Compensating saga: {}", sagaId);

        sagaEventRepository.save(SagaEvent.builder()
                .sagaId(sagaId)
                .eventType("COMPENSATION")
                .status(SagaStatus.COMPENSATING)
                .payload("Compensação iniciada")
                .build());
    }

    @KafkaListener(topics = KafkaConfig.PAYMENT_PROCESSED_TOPIC, groupId = "saga-group")
    @Transactional
    public void handlePaymentProcessed(String orderId) {
        log.info("Payment processed for order: {}", orderId);
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setOrderStatus("approved");
            orderRepository.save(order);
        });
    }

    @KafkaListener(topics = KafkaConfig.SELLER_NOTIFIED_TOPIC, groupId = "saga-group")
    public void handleSellerNotified(String orderId) {
        log.info("Seller notified for order: {}", orderId);
    }
}
