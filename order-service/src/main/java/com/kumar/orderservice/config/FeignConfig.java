package com.kumar.orderservice.config;

import com.kumar.orderservice.security.JwtContext;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {

            String authorization = JwtContext.getToken();


            if (authorization != null) {

                requestTemplate.header(
                        "Authorization",
                        authorization
                );


            }
        };
    }
}