package br.com.saga.service;

import br.com.saga.controller.CustomerController;
import br.com.saga.domain.entity.Customer;
import br.com.saga.dto.request.CustomerRequest;
import br.com.saga.dto.response.CustomerResponse;
import br.com.saga.exception.BusinessException;
import br.com.saga.exception.ResourceNotFoundException;
import br.com.saga.mapper.CustomerMapper;
import br.com.saga.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public Page<CustomerResponse> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::toResponseWithLinks);
    }

    public CustomerResponse findById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer não encontrado: " + id));
        return toResponseWithLinks(customer);
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsById(request.getCustomerId())) {
            throw new BusinessException("Customer já existe: " + request.getCustomerId());
        }
        Customer customer = Customer.builder()
                .customerId(request.getCustomerId())
                .customerUniqueId(request.getCustomerUniqueId())
                .customerZipCodePrefix(request.getCustomerZipCodePrefix())
                .customerCity(request.getCustomerCity())
                .customerState(request.getCustomerState())
                .build();
        customerRepository.save(customer);
        return toResponseWithLinks(customer);
    }

    @Transactional
    public CustomerResponse update(String id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer não encontrado: " + id));
        customer.setCustomerUniqueId(request.getCustomerUniqueId());
        customer.setCustomerZipCodePrefix(request.getCustomerZipCodePrefix());
        customer.setCustomerCity(request.getCustomerCity());
        customer.setCustomerState(request.getCustomerState());
        customerRepository.save(customer);
        return toResponseWithLinks(customer);
    }

    @Transactional
    public void delete(String id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer não encontrado: " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponse toResponseWithLinks(Customer customer) {
        CustomerResponse response = customerMapper.toResponse(customer);
        response.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(CustomerController.class).findById(customer.getCustomerId())).withSelfRel());
        response.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(CustomerController.class).findAll(null)).withRel("customers"));
        return response;
    }
}
