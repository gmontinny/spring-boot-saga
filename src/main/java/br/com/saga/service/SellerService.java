package br.com.saga.service;

import br.com.saga.controller.SellerController;
import br.com.saga.domain.entity.Seller;
import br.com.saga.dto.request.SellerRequest;
import br.com.saga.dto.response.SellerResponse;
import br.com.saga.exception.BusinessException;
import br.com.saga.exception.ResourceNotFoundException;
import br.com.saga.mapper.SellerMapper;
import br.com.saga.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;

    public Page<SellerResponse> findAll(Pageable pageable) {
        return sellerRepository.findAll(pageable).map(this::toResponseWithLinks);
    }

    public SellerResponse findById(String id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller não encontrado: " + id));
        return toResponseWithLinks(seller);
    }

    @Transactional
    public SellerResponse create(SellerRequest request) {
        if (sellerRepository.existsById(request.getSellerId())) {
            throw new BusinessException("Seller já existe: " + request.getSellerId());
        }
        Seller seller = Seller.builder()
                .sellerId(request.getSellerId())
                .sellerZipCodePrefix(request.getSellerZipCodePrefix())
                .sellerCity(request.getSellerCity())
                .sellerState(request.getSellerState())
                .build();
        sellerRepository.save(seller);
        return toResponseWithLinks(seller);
    }

    @Transactional
    public SellerResponse update(String id, SellerRequest request) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller não encontrado: " + id));
        seller.setSellerZipCodePrefix(request.getSellerZipCodePrefix());
        seller.setSellerCity(request.getSellerCity());
        seller.setSellerState(request.getSellerState());
        sellerRepository.save(seller);
        return toResponseWithLinks(seller);
    }

    @Transactional
    public void delete(String id) {
        if (!sellerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Seller não encontrado: " + id);
        }
        sellerRepository.deleteById(id);
    }

    private SellerResponse toResponseWithLinks(Seller seller) {
        SellerResponse response = sellerMapper.toResponse(seller);
        response.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(SellerController.class).findById(seller.getSellerId())).withSelfRel());
        response.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(SellerController.class).findAll(null)).withRel("sellers"));
        return response;
    }
}
