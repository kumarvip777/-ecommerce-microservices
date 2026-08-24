package com.kumar.productservice;

import com.kumar.productservice.dto.ProductRequestDTO;
import com.kumar.productservice.dto.ProductResponseDTO;
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
import com.kumar.productservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_ShouldCreateProduct_WhenValidDataProvided() {

        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO();

        requestDTO.setProductName("iPhone 15");
        requestDTO.setCategoryId(1L);
        requestDTO.setPrice(new BigDecimal("49999.00"));
        requestDTO.setStockQuantity(10);
        requestDTO.setDescription("Latest Apple smartphone");
        requestDTO.setBrand("Apple");
        requestDTO.setImageUrl("https://example.com/iphone.jpg");

        when(productRepository.existsByProductName("iPhone 15"))
                .thenReturn(false);

        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Mobile");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        Product savedProduct = new Product();

        savedProduct.setProductId(1L);
        savedProduct.setProductName("iPhone 15");
        savedProduct.setCategory(category);

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponseDTO responseDTO =
                new ProductResponseDTO();

        responseDTO.setProductName("iPhone 15");

        when(modelMapper.map(
                savedProduct,
                ProductResponseDTO.class))
                .thenReturn(responseDTO);

        // Act
        ProductResponseDTO result =
                productService.createProduct(requestDTO);

        // Assert
        assertNotNull(result);

        assertEquals(
                "iPhone 15",
                result.getProductName()
        );

        // Verify
        verify(productRepository)
                .save(any(Product.class));

        verify(categoryRepository)
                .findById(1L);

        verify(modelMapper)
                .map(
                        savedProduct,
                        ProductResponseDTO.class
                );

        // Capture saved Product
        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepository)
                .save(productCaptor.capture());

        Product capturedProduct =
                productCaptor.getValue();

        // Verify Product data
        assertEquals(
                "iPhone 15",
                capturedProduct.getProductName()
        );

        // Verify business logic
        assertEquals(
                ProductStatus.ACTIVE,
                capturedProduct.getStatus()
        );

        assertEquals(
                1L,
                capturedProduct.getCategory().getCategoryId()
        );

        // Verify save called exactly once
        verify(productRepository, times(1))
                .save(any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowException_WhenProductAlreadyExists() {
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setProductName("iPhone 15");
        when(productRepository.existsByProductName("iPhone 15"))
                .thenReturn(true);
        assertThrows(
                ProductAlreadyExistsException.class,
                () -> productService.createProduct(requestDTO)
        );

        verify(categoryRepository, never())
                .findById(anyLong());
        verify(productRepository, never())
                .save(any(Product.class));

    }

    @Test
    void createProduct_ShouldThrowException_WhenCategoryNotFound() {
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setProductName("iPhone 15");
        when(productRepository.existsByProductName("iPhone 15"))
                .thenReturn(false);
        requestDTO.setCategoryId(1L);
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(
                CategoryNotFoundException.class,
                () -> productService.createProduct(requestDTO)
        );


    }

    @Test
    void createProduct_ShouldSetOutOfStock_WhenStockIsZero() {

        // 1. Input
        ProductRequestDTO requestDTO =
                new ProductRequestDTO();

        requestDTO.setProductName("iPhone 15");
        requestDTO.setCategoryId(1L);

        // 2. Duplicate check → pass
        when(productRepository.existsByProductName("iPhone 15"))
                .thenReturn(false);

        // 3. Category → exists
        Category category = new Category();
        category.setCategoryId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        // 4. IMPORTANT INPUT
        requestDTO.setStockQuantity(0);

        // 5. Save dependency → mock
        Product savedProduct = new Product();
        savedProduct.setCategory(category);

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        // 6. ModelMapper dependency → mock
        ProductResponseDTO responseDTO =
                new ProductResponseDTO();

        when(modelMapper.map(
                savedProduct,
                ProductResponseDTO.class))
                .thenReturn(responseDTO);

        // 7. ACTUAL SERVICE METHOD
        productService.createProduct(requestDTO);

        // 8. Capture actual Product sent to save()
        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepository)
                .save(productCaptor.capture());

        Product capturedProduct =
                productCaptor.getValue();

        // 9. BUSINESS RULE
        assertEquals(
                ProductStatus.OUT_OF_STOCK,
                capturedProduct.getStatus()
        );


    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        Long productId = 1L;
        Product product = new Product();
        product.setProductId(1L);
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Mobile");

        product.setCategory(category);
        ProductResponseDTO responseDTO =
                new ProductResponseDTO();
        responseDTO.setProductId(1L);

        when(modelMapper.map(
                product,
                ProductResponseDTO.class))
                .thenReturn(responseDTO);
        ProductResponseDTO result =
                productService.getProductById(productId);
        assertNotNull(result);
        assertEquals(
                1L,
                result.getProductId()
        );
        assertEquals(
                1L,
                result.getProductId()
        );

    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        Long productId = 1L;
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(productId)
        );

    }

    @Test
    void getAllProducts_ShouldReturnProducts_WhenProductsExist() {

        // Arrange
        int page = 0;
        int size = 10;
        String sortBy = "productName";
        String direction = "asc";

        Product product = new Product();

        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Mobile");

        product.setCategory(category);

        List<Product> products =
                List.of(product);

        Page<Product> productPage =
                new PageImpl<>(products);

        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(productPage);

        ProductResponseDTO responseDTO =
                new ProductResponseDTO();

        responseDTO.setProductId(1L);

        when(modelMapper.map(
                product,
                ProductResponseDTO.class))
                .thenReturn(responseDTO);

        // Act
        Page<ProductResponseDTO> result =
                productService.getAllProducts(
                        page,
                        size,
                        sortBy,
                        direction
                );

        // Assert
        assertNotNull(result);

        assertEquals(
                1L,
                result.getContent().get(0).getProductId()
        );
    }

    @Test
    void getAllProducts_ShouldThrowException_WhenNoProductsFound() {
        Page<Product> productPage =
                new PageImpl<>(List.of());
        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(productPage);
        assertThrows(
                NoProductsFoundException.class,
                () -> productService.getAllProducts(
                        0,
                        10,
                        "productName",
                        "asc"
                )
        );

    }

    @Test
    void updateProduct_ShouldUpdateProduct_WhenValidDataProvided() {
        Long productId = 1L;
        UpdateProductDTO updateDTO =
                new UpdateProductDTO();
        updateDTO.setProductName("iPhone 16");
        Product product = new Product();
        product.setProductName("iPhone 15");
        product.setStockQuantity(10);
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        Category category = new Category();

        category.setCategoryId(1L);
        category.setCategoryName("Mobile");

        product.setCategory(category);
        updateDTO.setPrice(new BigDecimal("59999.00"));
        updateDTO.setStockQuantity(20);
        updateDTO.setCategoryId(2L);
        Category updatedCategory = new Category();
        updatedCategory.setCategoryId(2L);
        updatedCategory.setCategoryName("Laptop");
        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(updatedCategory));
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);
        ProductResponseDTO responseDTO =
                new ProductResponseDTO();

        when(modelMapper.map(
                product,
                ProductResponseDTO.class))
                .thenReturn(responseDTO);

        productService.updateProduct(
                productId,
                updateDTO
        );
        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepository)
                .save(productCaptor.capture());
        Product capturedProduct =
                productCaptor.getValue();
        assertEquals(
                "iPhone 16",
                capturedProduct.getProductName()
        );
        assertEquals(
                new BigDecimal("59999.00"),
                capturedProduct.getPrice()
        );
        assertEquals(
                20,
                capturedProduct.getStockQuantity()
        );
        assertEquals(
                2L,
                capturedProduct.getCategory().getCategoryId()
        );
        assertEquals(
                ProductStatus.ACTIVE,
                capturedProduct.getStatus()
        );


    }

    @Test
    void updateProduct_ShouldThrowException_WhenUpdateDTOIsEmpty() {
        UpdateProductDTO updateDTO =
                new UpdateProductDTO();
        assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(
                        1L,
                        updateDTO
                )
        );

    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {

        // Arrange
        Long productId = 1L;

        UpdateProductDTO updateDTO =
                new UpdateProductDTO();

        updateDTO.setProductName("iPhone 16");

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(
                        productId,
                        updateDTO
                )
        );

        // Verify
        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void updateProduct_ShouldThrowException_WhenUpdatedProductNameAlreadyExists() {
        Long productId = 1L;
        Product product = new Product();
        product.setProductName("iPhone 15");
        UpdateProductDTO updateDTO =
                new UpdateProductDTO();

        updateDTO.setProductName("Samsung S25");
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        when(productRepository.existsByProductName("Samsung S25"))
                .thenReturn(true);
        assertThrows(
                ProductAlreadyExistsException.class,
                () -> productService.updateProduct(
                        productId,
                        updateDTO
                )
        );
        verify(productRepository, never())
                .save(any(Product.class));

    }

    @Test
    void updateProduct_ShouldThrowException_WhenCategoryNotFound() {
        Long productId = 1L;
        Product product = new Product();

        product.setProductName("iPhone 15");
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        UpdateProductDTO updateDTO =
                new UpdateProductDTO();

        updateDTO.setCategoryId(2L);
        when(categoryRepository.findById(2L))
                .thenReturn(Optional.empty());
        assertThrows(
                CategoryNotFoundException.class,
                () -> productService.updateProduct(
                        productId,
                        updateDTO
                )
        );
        verify(productRepository, never())
                .save(any(Product.class));

    }

    @Test
    void deleteProduct_ShouldDeleteProduct_WhenProductExists() {
        Long productId = 1L;
        Product product = new Product();

        product.setProductId(1L);
        product.setProductName("iPhone 15");
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));
        productService.deleteProduct(productId);
        verify(productRepository)
                .delete(product);


    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductNotFound() {
        Long productId = 1L;
        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(productId)
        );
        verify(productRepository, never())
                .delete(any(Product.class));

    }
}