package br.com.saga.service;

import br.com.saga.controller.ProductController;
import br.com.saga.domain.entity.Product;
import br.com.saga.dto.request.ProductRequest;
import br.com.saga.dto.response.ProductResponse;
import br.com.saga.exception.BusinessException;
import br.com.saga.exception.ResourceNotFoundException;
import br.com.saga.mapper.ProductMapper;
import br.com.saga.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toResponseWithLinks);
    }

    public ProductResponse findById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product não encontrado: " + id));
        return toResponseWithLinks(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsById(request.getProductId())) {
            throw new BusinessException("Product já existe: " + request.getProductId());
        }
        Product product = Product.builder()
                .productId(request.getProductId())
                .productCategoryName(request.getProductCategoryName())
                .productNameLength(request.getProductNameLength())
                .productDescriptionLength(request.getProductDescriptionLength())
                .productPhotosQty(request.getProductPhotosQty())
                .productWeightG(request.getProductWeightG())
                .productLengthCm(request.getProductLengthCm())
                .productHeightCm(request.getProductHeightCm())
                .productWidthCm(request.getProductWidthCm())
                .build();
        productRepository.save(product);
        return toResponseWithLinks(product);
    }

    @Transactional
    public ProductResponse update(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product não encontrado: " + id));
        product.setProductCategoryName(request.getProductCategoryName());
        product.setProductNameLength(request.getProductNameLength());
        product.setProductDescriptionLength(request.getProductDescriptionLength());
        product.setProductPhotosQty(request.getProductPhotosQty());
        product.setProductWeightG(request.getProductWeightG());
        product.setProductLengthCm(request.getProductLengthCm());
        product.setProductHeightCm(request.getProductHeightCm());
        product.setProductWidthCm(request.getProductWidthCm());
        productRepository.save(product);
        return toResponseWithLinks(product);
    }

    @Transactional
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product não encontrado: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse toResponseWithLinks(Product product) {
        ProductResponse response = productMapper.toResponse(product);
        response.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(ProductController.class).findById(product.getProductId())).withSelfRel());
        response.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(ProductController.class).findAll(null)).withRel("products"));
        return response;
    }
}
