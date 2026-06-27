package br.com.saga.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerNotificationEvent {

    private String sellerId;
    private String orderId;
    private String customerId;
    private LocalDateTime orderDate;
    private List<ItemDetail> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDetail {
        private String productId;
        private BigDecimal price;
        private BigDecimal freightValue;
    }
}
