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
@Table(name = "sellers")
public class Seller {

    @Id
    @Column(name = "seller_id", length = 64)
    private String sellerId;

    @Column(name = "seller_zip_code_prefix", length = 10)
    private String sellerZipCodePrefix;

    @Column(name = "seller_city", length = 100)
    private String sellerCity;

    @Column(name = "seller_state", length = 2)
    private String sellerState;
}
