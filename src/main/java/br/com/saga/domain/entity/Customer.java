package br.com.saga.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "customer_id", length = 64)
    private String customerId;

    @Column(name = "customer_unique_id", nullable = false, length = 64)
    private String customerUniqueId;

    @Column(name = "customer_zip_code_prefix", length = 10)
    private String customerZipCodePrefix;

    @Column(name = "customer_city", length = 100)
    private String customerCity;

    @Column(name = "customer_state", length = 2)
    private String customerState;
}
