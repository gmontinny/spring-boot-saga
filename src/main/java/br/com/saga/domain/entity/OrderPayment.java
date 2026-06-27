package br.com.saga.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_payments")
public class OrderPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "payment_sequential")
    private Integer paymentSequential;

    @Column(name = "payment_type", length = 30)
    private String paymentType;

    @Column(name = "payment_installments")
    private Integer paymentInstallments;

    @Column(name = "payment_value", precision = 10, scale = 2)
    private BigDecimal paymentValue;
}
