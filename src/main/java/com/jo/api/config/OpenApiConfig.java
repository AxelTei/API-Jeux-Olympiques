package com.jo.api.config;

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
                        .title("JO API Documentation")
                        .description("Documentation de l'API développée avec Spring Boot")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Axel Teisseire")
                                .email("mr.teisseire@gmail.com"))
                        .license(new License()
                                .name("License")
                                .url("https://example.com/license")));
    }
}
