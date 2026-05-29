package com.generated.fashionecommercewebsite.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("fashion-ecommerce-website API")
                .version("1.0.0")
                .description("Generate a NEW full-stack Angular + Spring Boot fashion ecommerce app named StyleHub. ## MOST IMPORTANT GOAL Create a premium futuristic fashion ecommerce website, not a basic CRUD")
                .contact(new Contact()
                    .name("API Support")
                    .url("https://example.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
