package br.com.saga.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = false)
public class OrderResponse extends RepresentationModel<OrderResponse> {
    private String orderId;
    private String customerId;
    private String orderStatus;
    private LocalDateTime orderPurchaseTimestamp;
    private LocalDateTime orderEstimatedDeliveryDate;
    private List<OrderItemResponse> items;
    private List<PaymentResponse> payments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private String productId;
        private String sellerId;
        private BigDecimal price;
        private BigDecimal freightValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponse {
        private String paymentType;
        private Integer paymentInstallments;
        private BigDecimal paymentValue;
    }
}
