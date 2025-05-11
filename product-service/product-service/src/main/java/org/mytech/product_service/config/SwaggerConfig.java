package org.mytech.product_service.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customerOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .description("Handles everything related to products")
                        .version("1.0.0"));
    }
    @Bean
    public GroupedOpenApi productGroup() {
        return GroupedOpenApi.builder()
                .group("product-service")
                .pathsToMatch("/products/**")
                .build();
    }
}
