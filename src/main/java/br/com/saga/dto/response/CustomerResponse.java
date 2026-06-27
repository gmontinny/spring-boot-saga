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
public class CustomerResponse extends RepresentationModel<CustomerResponse> {
    private String customerId;
    private String customerUniqueId;
    private String customerZipCodePrefix;
    private String customerCity;
    private String customerState;
}
