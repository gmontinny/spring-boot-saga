package br.com.saga.mapper;

import br.com.saga.domain.entity.Order;
import br.com.saga.domain.entity.OrderItem;
import br.com.saga.domain.entity.OrderPayment;
import br.com.saga.dto.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    OrderResponse.OrderItemResponse toItemResponse(OrderItem item);

    OrderResponse.PaymentResponse toPaymentResponse(OrderPayment payment);
}
