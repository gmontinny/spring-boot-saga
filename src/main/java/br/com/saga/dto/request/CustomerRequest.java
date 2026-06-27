package br.com.saga.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotBlank(message = "Customer ID é obrigatório")
    private String customerId;

    @NotBlank(message = "Customer Unique ID é obrigatório")
    private String customerUniqueId;

    @Size(max = 10)
    private String customerZipCodePrefix;

    @Size(max = 100)
    private String customerCity;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String customerState;
}
