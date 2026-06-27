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
public class SellerResponse extends RepresentationModel<SellerResponse> {
    private String sellerId;
    private String sellerZipCodePrefix;
    private String sellerCity;
    private String sellerState;
}
