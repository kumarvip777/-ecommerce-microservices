package com.kumar.productservice.controller;

import com.kumar.productservice.dto.ProductRequestDTO;
import com.kumar.productservice.dto.ProductResponseDTO;
import com.kumar.productservice.dto.StockUpdateRequestDTO;
import com.kumar.productservice.dto.UpdateProductDTO;
import com.kumar.productservice.payload.ApiResponse;
import com.kumar.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(
        name = "Product Management",
        description = "APIs for managing products"
)
public class ProductController {

    private static final Logger logger =
            LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Operation(
            summary = "Create Product",
            description = "Creates a new product"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(

            @Valid
            @RequestBody ProductRequestDTO requestDTO,

            HttpServletRequest request) {

        logger.info("Received request to create product");

        ProductResponseDTO responseDTO =
                productService.createProduct(requestDTO);

        ApiResponse<ProductResponseDTO> response =
                ApiResponse.success(
                        responseDTO,
                        "Product created successfully",
                        request.getRequestURI()
                );

        logger.info(
                "Product created successfully. productId={}",
                responseDTO.getProductId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get Product By Id",
            description = "Returns product details using product id"
    )
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(

            @PathVariable Long productId,

            HttpServletRequest request) {

        logger.info(
                "Received request to fetch productId: {}",
                productId
        );

        ProductResponseDTO responseDTO =
                productService.getProductById(productId);

        ApiResponse<ProductResponseDTO> response =
                ApiResponse.success(
                        responseDTO,
                        "Product fetched successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Reduce Product Stock",
            description = "Reduces product stock after successful order placement"
    )
    @PutMapping("/{productId}/stock")
    public ResponseEntity<ApiResponse<String>> reduceStock(

            @PathVariable Long productId,

            @Valid
            @RequestBody StockUpdateRequestDTO requestDTO, HttpServletRequest request) {
        logger.info("==== REDUCE STOCK API HIT ====");

        productService.reduceStock(productId, requestDTO);
        ApiResponse<String> response =
                ApiResponse.success(
                        "Stock updated successfully",
                        "Stock updated successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);


    }

    @Operation(
            summary = "Get All Products",
            description = "Returns paginated list of products"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> getAllProducts(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "productId") String sortBy,

            @RequestParam(defaultValue = "asc") String direction,

            HttpServletRequest request) {

        logger.info("Received request to fetch all products");

        Page<ProductResponseDTO> responseDTO =
                productService.getAllProducts(
                        page,
                        size,
                        sortBy,
                        direction
                );

        ApiResponse<Page<ProductResponseDTO>> response =
                ApiResponse.success(
                        responseDTO,
                        "Products fetched successfully",
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update Product",
            description = "Updates product details"
    )
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(

            @PathVariable Long productId,

            @Valid
            @RequestBody UpdateProductDTO updateDTO,

            HttpServletRequest request) {

        logger.info(
                "Received request to update productId: {}",
                productId
        );

        ProductResponseDTO responseDTO =
                productService.updateProduct(
                        productId,
                        updateDTO
                );

        ApiResponse<ProductResponseDTO> response =
                ApiResponse.success(
                        responseDTO,
                        "Product updated successfully",
                        request.getRequestURI()
                );

        logger.info(
                "Product updated successfully. productId={}",
                productId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete Product",
            description = "Deletes product by product id"
    )
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(

            @PathVariable Long productId,

            HttpServletRequest request) {

        logger.info(
                "Received request to delete productId: {}",
                productId
        );

        productService.deleteProduct(productId);

        ApiResponse<String> response =
                ApiResponse.success(
                        "Product deleted successfully",
                        "Product deleted successfully",
                        request.getRequestURI()
                );

        logger.info(
                "Product deleted successfully. productId={}",
                productId
        );

        return ResponseEntity.ok(response);
    }
}