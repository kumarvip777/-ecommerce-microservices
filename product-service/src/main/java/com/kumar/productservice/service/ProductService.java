package com.kumar.productservice.service;

import com.kumar.productservice.dto.ProductRequestDTO;
import com.kumar.productservice.dto.ProductResponseDTO;
import com.kumar.productservice.dto.UpdateProductDTO;
import org.springframework.data.domain.Page;
import com.kumar.productservice.dto.StockUpdateRequestDTO;

public interface ProductService {

    // Create Product
    ProductResponseDTO createProduct(
            ProductRequestDTO requestDTO
    );

    // Get Product By Id
    ProductResponseDTO getProductById(
            Long productId
    );

    // Get All Products (Pagination)
    Page<ProductResponseDTO> getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction
    );

    // Update Product
    ProductResponseDTO updateProduct(
            Long productId,
            UpdateProductDTO updateDTO
    );

    // Delete Product
    void deleteProduct(
            Long productId
    );

    // Reduce Product Stock
    void reduceStock(
            Long productId,
            StockUpdateRequestDTO requestDTO
    );

}