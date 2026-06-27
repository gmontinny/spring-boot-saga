package br.com.saga.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = false)
public class ProductResponse extends RepresentationModel<ProductResponse> {
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
