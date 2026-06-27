package br.com.saga.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "order_id", length = 64)
    private String orderId;

    @Column(name = "customer_id", nullable = false, length = 64)
    private String customerId;

    @Column(name = "order_status", length = 30)
    private String orderStatus;

    @Column(name = "order_purchase_timestamp")
    private LocalDateTime orderPurchaseTimestamp;

    @Column(name = "order_approved_at")
    private LocalDateTime orderApprovedAt;

    @Column(name = "order_delivered_carrier_date")
    private LocalDateTime orderDeliveredCarrierDate;

    @Column(name = "order_delivered_customer_date")
    private LocalDateTime orderDeliveredCustomerDate;

    @Column(name = "order_estimated_delivery_date")
    private LocalDateTime orderEstimatedDeliveryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;
}
