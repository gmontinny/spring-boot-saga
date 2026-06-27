package br.com.saga.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "order_item_id")
    private Integer orderItemId;

    @Column(name = "product_id", length = 64)
    private String productId;

    @Column(name = "seller_id", length = 64)
    private String sellerId;

    @Column(name = "shipping_limit_date")
    private LocalDateTime shippingLimitDate;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "freight_value", precision = 10, scale = 2)
    private BigDecimal freightValue;
}
