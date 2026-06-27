package br.com.saga.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {

    @NotBlank(message = "Customer ID é obrigatório")
    private String customerId;

    @NotEmpty(message = "Itens do pedido são obrigatórios")
    @Valid
    private List<OrderItemRequest> items;

    @Valid
    private PaymentRequest payment;

    @Data
    public static class OrderItemRequest {
        @NotBlank(message = "Product ID é obrigatório")
        private String productId;

        @NotBlank(message = "Seller ID é obrigatório")
        private String sellerId;

        private BigDecimal price;
        private BigDecimal freightValue;
    }

    @Data
    public static class PaymentRequest {
        @NotBlank(message = "Tipo de pagamento é obrigatório")
        private String paymentType;

        private Integer installments;
        private BigDecimal value;
    }
}
