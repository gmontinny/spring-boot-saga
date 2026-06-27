package br.com.saga.mapper;

import br.com.saga.domain.entity.Seller;
import br.com.saga.dto.response.SellerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerMapper {
    SellerResponse toResponse(Seller seller);
}
