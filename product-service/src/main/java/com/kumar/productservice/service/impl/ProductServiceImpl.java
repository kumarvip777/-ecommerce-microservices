package com.kumar.productservice.service.impl;

import com.kumar.productservice.dto.ProductRequestDTO;
import com.kumar.productservice.dto.ProductResponseDTO;
import com.kumar.productservice.dto.StockUpdateRequestDTO;
import com.kumar.productservice.dto.UpdateProductDTO;
import com.kumar.productservice.entity.Category;
import com.kumar.productservice.entity.Product;
import com.kumar.productservice.entity.enums.ProductStatus;
import com.kumar.productservice.exception.category.CategoryNotFoundException;
import com.kumar.productservice.exception.product.NoProductsFoundException;
import com.kumar.productservice.exception.product.ProductAlreadyExistsException;
import com.kumar.productservice.exception.product.ProductNotFoundException;
import com.kumar.productservice.repo.CategoryRepository;
import com.kumar.productservice.repo.ProductRepository;
import com.kumar.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger logger =
            LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(
            @Valid ProductRequestDTO requestDTO) {


        logger.info(
                "Starting product creation for product: {}",
                requestDTO.getProductName()
        );

        String productName =
                requestDTO.getProductName().trim();

        if (productRepository.existsByProductName(productName)) {

            throw new ProductAlreadyExistsException(
                    "Product already exists: " + productName
            );
        }

        Category category =
                categoryRepository.findById(
                                requestDTO.getCategoryId()
                        )
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found with id: "
                                                + requestDTO.getCategoryId()
                                )
                        );

        Product product = new Product();

        product.setProductName(productName);
        product.setDescription(requestDTO.getDescription());
        product.setBrand(requestDTO.getBrand());
        product.setPrice(requestDTO.getPrice());
        product.setStockQuantity(requestDTO.getStockQuantity());
        product.setImageUrl(requestDTO.getImageUrl());
        product.setCategory(category);


        if (requestDTO.getStockQuantity() == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else {
            product.setStatus(ProductStatus.ACTIVE);
        }

        Product savedProduct =
                productRepository.save(product);

        logger.info(
                "Product created successfully. productId={}, productName={}",
                savedProduct.getProductId(),
                savedProduct.getProductName()
        );

        ProductResponseDTO responseDTO =
                modelMapper.map(
                        savedProduct,
                        ProductResponseDTO.class
                );

        responseDTO.setCategoryId(
                savedProduct.getCategory().getCategoryId()
        );

        responseDTO.setCategoryName(
                savedProduct.getCategory().getCategoryName()
        );

        return responseDTO;
    }

    @Override
    public ProductResponseDTO getProductById(
            Long productId) {

        logger.info(
                "Fetching product with id: {}",
                productId
        );

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + productId
                                )
                        );

        ProductResponseDTO responseDTO =
                modelMapper.map(
                        product,
                        ProductResponseDTO.class
                );

        responseDTO.setCategoryId(
                product.getCategory().getCategoryId()
        );

        responseDTO.setCategoryName(
                product.getCategory().getCategoryName()
        );

        logger.info(
                "Product fetched successfully. productId={}",
                productId
        );

        return responseDTO;
    }

    @Override
    public Page<ProductResponseDTO> getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction) {

        logger.info("Fetching all products");

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );

        Page<Product> productPage =
                productRepository.findAll(pageable);

        if (productPage.isEmpty()) {

            throw new NoProductsFoundException(
                    "No products found"
            );
        }

        logger.info(
                "Fetched {} products",
                productPage.getTotalElements()
        );

        return productPage.map(product -> {

            ProductResponseDTO dto =
                    modelMapper.map(
                            product,
                            ProductResponseDTO.class
                    );

            dto.setCategoryId(
                    product.getCategory().getCategoryId()
            );

            dto.setCategoryName(
                    product.getCategory().getCategoryName()
            );

            return dto;
        });
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(
            Long productId,
            @Valid UpdateProductDTO updateDTO) {

        logger.info(
                "Updating product with id: {}",
                productId
        );

        if (updateDTO.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one field must be provided for update"
            );
        }

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + productId
                                )
                        );

        applyProductNameUpdate(product, updateDTO);
        applyDescriptionUpdate(product, updateDTO);
        applyBrandUpdate(product, updateDTO);
        applyPriceUpdate(product, updateDTO);
        applyStockUpdate(product, updateDTO);
        applyCategoryUpdate(product, updateDTO);
        applyImageUrlUpdate(product, updateDTO);

        updateProductStatus(product);

        Product updatedProduct =
                productRepository.save(product);

        logger.info(
                "Product updated successfully. productId={}",
                updatedProduct.getProductId()
        );

        ProductResponseDTO responseDTO =
                modelMapper.map(
                        updatedProduct,
                        ProductResponseDTO.class
                );

        responseDTO.setCategoryId(
                updatedProduct.getCategory().getCategoryId()
        );

        responseDTO.setCategoryName(
                updatedProduct.getCategory().getCategoryName()
        );

        return responseDTO;
    }

    @Override
    @Transactional
    public void deleteProduct(
            Long productId) {

        logger.info(
                "Deleting product with id: {}",
                productId
        );

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + productId
                                )
                        );

        productRepository.delete(product);

        logger.info(
                "Product deleted successfully. productId={}",
                productId
        );
    }

    @Transactional
    @Override
    public void reduceStock(Long productId, StockUpdateRequestDTO requestDTO) {


        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: " + productId
                                )
                        );
        logger.info("Before Stock : {}", product.getStockQuantity());

        if (product.getStockQuantity() < requestDTO.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock available");
        }
        product.setStockQuantity(
                product.getStockQuantity() - requestDTO.getQuantity()
        );
        if (product.getStockQuantity() == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else {
            product.setStatus(ProductStatus.ACTIVE);
        }
        logger.info(
                "Stock reduced successfully. productId={}, remainingStock={}",
                productId,
                product.getStockQuantity()
        );
        logger.info("After Stock : {}", product.getStockQuantity());


    }

    private void applyProductNameUpdate(
            Product product,
            UpdateProductDTO updateDTO) {

        if (updateDTO.getProductName() == null
                || updateDTO.getProductName().isBlank()) {
            return;
        }

        String productName =
                updateDTO.getProductName().trim();

        if (productRepository.existsByProductName(productName)
                && !product.getProductName().equalsIgnoreCase(productName)) {

            throw new ProductAlreadyExistsException(
                    "Product already exists: " + productName
            );
        }

        product.setProductName(productName);

        logger.info("Product name updated");
    }

    private void applyDescriptionUpdate(
            Product product,
            UpdateProductDTO updateDTO) {

        if (updateDTO.getDescription() == null
                || updateDTO.getDescription().isBlank()) {
            return;
        }

        product.setDescription(
                updateDTO.getDescription().trim()
        );

        logger.info("Product description updated");
    }

    private void applyBrandUpdate(
            Product product,
            UpdateProductDTO updateDTO) {

        if (updateDTO.getBrand() == null
                || updateDTO.getBrand().isBlank()) {
            return;
        }

        product.setBrand(
                updateDTO.getBrand().trim()
        );

        logger.info("Product brand updated");
    }

    private void applyPriceUpdate(
            Product product,
            UpdateProductDTO updateDTO) {

        if (updateDTO.getPrice() == null) {
            return;
        }

        product.setPrice(updateDTO.getPrice());

        logger.info("Product price updated");
    }

    private void applyStockUpdate(
            Product product,
            UpdateProductDTO updateDTO) {

        if (updateDTO.getStockQuantity() == null) {
            return;
        }

        product.setStockQuantity(
                updateDTO.getStockQuantity()
        );

        logger.info("Product stock updated");
    }

    private void applyCategoryUpdate(
            Product product,
            UpdateProductDTO updateDTO) {

        if (updateDTO.getCategoryId() == null) {
            return;
        }

        Category category =
                categoryRepository.findById(
                                updateDTO.getCategoryId()
                        )
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found with id: "
                                                + updateDTO.getCategoryId()
                                )
                        );

        product.setCategory(category);

        logger.info("Product category updated");
    }

    private void updateProductStatus(
            Product product) {

        if (product.getStockQuantity() == 0) {

            product.setStatus(
                    ProductStatus.OUT_OF_STOCK
            );

        } else {

            product.setStatus(
                    ProductStatus.ACTIVE
            );
        }
    }

    private void applyImageUrlUpdate(
            Product product,
            UpdateProductDTO updateDTO) {

        if (updateDTO.getImageUrl() == null
                || updateDTO.getImageUrl().isBlank()) {
            return;
        }

        product.setImageUrl(
                updateDTO.getImageUrl().trim()
        );

        logger.info("Product image updated");
    }
}