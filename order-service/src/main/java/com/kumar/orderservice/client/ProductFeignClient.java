package com.kumar.orderservice.client;

import com.kumar.orderservice.config.FeignConfig;
import com.kumar.orderservice.dto.external.ApiResponse;
import com.kumar.orderservice.dto.external.ProductResponse;
import com.kumar.orderservice.dto.external.StockUpdateRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PRODUCT-SERVICE",configuration = FeignConfig.class)

public interface ProductFeignClient {

    @GetMapping("/api/products/{productId}")
    ApiResponse<ProductResponse> getProductById(
            @PathVariable("productId") Long productId
    );

    @PutMapping("/api/products/{productId}/stock")
    void reduceStock(
            @PathVariable("productId") Long productId,
            @RequestBody StockUpdateRequestDTO requestDTO
    );


}
