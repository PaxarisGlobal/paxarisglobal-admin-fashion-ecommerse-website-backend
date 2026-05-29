package com.generated.fashionecommercewebsite.service.impl;

import com.generated.fashionecommercewebsite.dto.ProductDto;
import com.generated.fashionecommercewebsite.entity.Product;
import com.generated.fashionecommercewebsite.exception.ResourceNotFoundException;
import com.generated.fashionecommercewebsite.repository.ProductRepository;
import com.generated.fashionecommercewebsite.service.ProductService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDto> findAll() {
        return productRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public ProductDto findById(UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return toDto(product);
    }

    @Override
    public ProductDto create(ProductDto dto) {
        Product product = toEntity(dto);
        product.setId(null);
        return toDto(productRepository.save(product));
    }

    @Override
    public ProductDto update(UUID id, ProductDto dto) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStockQuantity(dto.getStockQuantity());
        existing.setBrand(dto.getBrand());
        existing.setCategory(dto.getCategory());
        existing.setImageUrl(dto.getImageUrl());
        return toDto(productRepository.save(existing));
    }

    @Override
    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDto toDto(Product entity) {
        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setStockQuantity(entity.getStockQuantity());
        dto.setBrand(entity.getBrand());
        dto.setCategory(entity.getCategory());
        dto.setImageUrl(entity.getImageUrl());
        return dto;
    }

    private Product toEntity(ProductDto dto) {
        Product entity = new Product();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setStockQuantity(dto.getStockQuantity());
        entity.setBrand(dto.getBrand());
        entity.setCategory(dto.getCategory());
        entity.setImageUrl(dto.getImageUrl());
        return entity;
    }
}
