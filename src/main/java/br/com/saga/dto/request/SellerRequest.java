package br.com.saga.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SellerRequest {

    @NotBlank(message = "Seller ID é obrigatório")
    private String sellerId;

    @Size(max = 10)
    private String sellerZipCodePrefix;

    @Size(max = 100)
    private String sellerCity;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String sellerState;
}
