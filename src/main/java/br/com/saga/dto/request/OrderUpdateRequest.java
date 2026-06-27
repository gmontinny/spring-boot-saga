package br.com.saga.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderUpdateRequest {

    @NotBlank(message = "Status é obrigatório")
    private String orderStatus;
}
