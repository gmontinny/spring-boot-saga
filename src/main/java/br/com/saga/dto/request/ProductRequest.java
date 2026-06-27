package br.com.saga.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product ID é obrigatório")
    private String productId;

    private String productCategoryName;
    private Integer productNameLength;
    private Integer productDescriptionLength;
    private Integer productPhotosQty;
    private Integer productWeightG;
    private Integer productLengthCm;
    private Integer productHeightCm;
    private Integer productWidthCm;
}
