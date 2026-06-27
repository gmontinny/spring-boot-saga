package br.com.saga.mapper;

import br.com.saga.domain.entity.Customer;
import br.com.saga.dto.response.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
    CustomerResponse toResponse(Customer customer);
}
