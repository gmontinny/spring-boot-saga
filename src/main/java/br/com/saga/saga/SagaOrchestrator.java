package br.com.saga.saga;

import br.com.saga.config.KafkaConfig;
import br.com.saga.domain.entity.*;
import br.com.saga.domain.enums.SagaStatus;
import br.com.saga.dto.request.OrderRequest;
import br.com.saga.dto.response.OrderResponse;
import br.com.saga.exception.BusinessException;
import br.com.saga.exception.ResourceNotFoundException;
import br.com.saga.mapper.OrderMapper;
import br.com.saga.repository.*;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaOrchestrator {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final SagaEventRepository sagaEventRepository;
    private final OrderMapper orderMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        String sagaId = UUID.randomUUID().toString();

        customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer não encontrado: " + request.getCustomerId()));

        for (var item : request.getItems()) {
            sellerRepository.findById(item.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller não encontrado: " + item.getSellerId()));
            productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product não encontrado: " + item.getProductId()));
        }

        saveSagaEvent(sagaId, "ORDER_CREATION", SagaStatus.STARTED, request);

        try {
            Order order = Order.builder()
                    .orderId(UUID.randomUUID().toString().replace("-", ""))
                    .customerId(request.getCustomerId())
                    .orderStatus("created")
                    .orderPurchaseTimestamp(LocalDateTime.now())
                    .build();
            orderRepository.save(order);

            for (int i = 0; i < request.getItems().size(); i++) {
                var item = request.getItems().get(i);
                orderItemRepository.save(OrderItem.builder()
                        .orderId(order.getOrderId())
                        .orderItemId(i + 1)
                        .productId(item.getProductId())
                        .sellerId(item.getSellerId())
                        .price(item.getPrice())
                        .freightValue(item.getFreightValue())
                        .build());
            }

            if (request.getPayment() != null) {
                orderPaymentRepository.save(OrderPayment.builder()
                        .orderId(order.getOrderId())
                        .paymentSequential(1)
                        .paymentType(request.getPayment().getPaymentType())
                        .paymentInstallments(request.getPayment().getInstallments())
                        .paymentValue(request.getPayment().getValue())
                        .build());
            }

            saveSagaEvent(sagaId, "ORDER_CREATION", SagaStatus.COMPLETED, order);
            kafkaTemplate.send(KafkaConfig.ORDER_CREATED_TOPIC, order.getOrderId(), order);

            request.getItems().stream()
                    .map(OrderRequest.OrderItemRequest::getSellerId)
                    .distinct()
                    .forEach(sellerId -> kafkaTemplate.send(KafkaConfig.SELLER_NOTIFIED_TOPIC, sellerId, order.getOrderId()));

            OrderResponse response = orderMapper.toResponse(order);
            response.setItems(request.getItems().stream()
                    .map(i -> OrderResponse.OrderItemResponse.builder()
                            .productId(i.getProductId())
                            .sellerId(i.getSellerId())
                            .price(i.getPrice())
                            .freightValue(i.getFreightValue())
                            .build()).toList());
            return response;

        } catch (Exception e) {
            saveSagaEvent(sagaId, "ORDER_CREATION", SagaStatus.FAILED, e.getMessage());
            kafkaTemplate.send(KafkaConfig.ORDER_COMPENSATE_TOPIC, sagaId, sagaId);
            throw new BusinessException("Falha ao criar pedido: " + e.getMessage());
        }
    }

    private void saveSagaEvent(String sagaId, String eventType, SagaStatus status, Object payload) {
        String payloadStr;
        try {
            payloadStr = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            payloadStr = payload.toString();
        }

        sagaEventRepository.save(SagaEvent.builder()
                .sagaId(sagaId)
                .eventType(eventType)
                .status(status)
                .payload(payloadStr)
                .build());
    }
}
