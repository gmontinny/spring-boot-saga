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
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id", length = 64)
    private String productId;

    @Column(name = "product_category_name", length = 100)
    private String productCategoryName;

    @Column(name = "product_name_lenght")
    private Integer productNameLength;

    @Column(name = "product_description_lenght")
    private Integer productDescriptionLength;

    @Column(name = "product_photos_qty")
    private Integer productPhotosQty;

    @Column(name = "product_weight_g")
    private Integer productWeightG;

    @Column(name = "product_length_cm")
    private Integer productLengthCm;

    @Column(name = "product_height_cm")
    private Integer productHeightCm;

    @Column(name = "product_width_cm")
    private Integer productWidthCm;
}
