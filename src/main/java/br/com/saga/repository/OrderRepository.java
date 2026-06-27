package br.com.saga.repository;

import br.com.saga.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByCustomerId(String customerId, Pageable pageable);
    Page<Order> findByOrderStatus(String status, Pageable pageable);
}
