package br.com.saga.service;

import br.com.saga.controller.OrderController;
import br.com.saga.domain.entity.Order;
import br.com.saga.dto.request.OrderUpdateRequest;
import br.com.saga.dto.response.OrderResponse;
import br.com.saga.exception.ResourceNotFoundException;
import br.com.saga.mapper.OrderMapper;
import br.com.saga.repository.OrderItemRepository;
import br.com.saga.repository.OrderPaymentRepository;
import br.com.saga.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final OrderMapper orderMapper;

    public Page<OrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponseWithLinks);
    }

    public OrderResponse findById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrado: " + id));
        return toFullResponse(order);
    }

    public Page<OrderResponse> findByCustomerId(String customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable).map(this::toResponseWithLinks);
    }

    @Transactional
    public OrderResponse updateStatus(String id, OrderUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrado: " + id));
        order.setOrderStatus(request.getOrderStatus());
        orderRepository.save(order);
        return toFullResponse(order);
    }

    @Transactional
    public void delete(String id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order não encontrado: " + id);
        }
        orderItemRepository.findByOrderId(id).forEach(item -> orderItemRepository.delete(item));
        orderPaymentRepository.findByOrderId(id).forEach(payment -> orderPaymentRepository.delete(payment));
        orderRepository.deleteById(id);
    }

    private OrderResponse toResponseWithLinks(Order order) {
        OrderResponse response = orderMapper.toResponse(order);
        addLinks(response, order.getOrderId());
        return response;
    }

    private OrderResponse toFullResponse(Order order) {
        OrderResponse response = orderMapper.toResponse(order);
        response.setItems(orderItemRepository.findByOrderId(order.getOrderId())
                .stream().map(orderMapper::toItemResponse).toList());
        response.setPayments(orderPaymentRepository.findByOrderId(order.getOrderId())
                .stream().map(orderMapper::toPaymentResponse).toList());
        addLinks(response, order.getOrderId());
        return response;
    }

    private void addLinks(OrderResponse response, String orderId) {
        response.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(OrderController.class).findById(orderId)).withSelfRel());
        response.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(OrderController.class).findAll(null)).withRel("orders"));
    }
}
