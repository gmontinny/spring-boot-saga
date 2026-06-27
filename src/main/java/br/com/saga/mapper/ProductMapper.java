package br.com.saga.mapper;

import br.com.saga.domain.entity.Product;
import br.com.saga.dto.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductResponse toResponse(Product product);
}
