package br.com.saga.repository;

import br.com.saga.domain.entity.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {
    List<OrderPayment> findByOrderId(String orderId);
}
