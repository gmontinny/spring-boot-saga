package br.com.saga.repository;

import br.com.saga.domain.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    Page<Customer> findByCustomerState(String state, Pageable pageable);
}
