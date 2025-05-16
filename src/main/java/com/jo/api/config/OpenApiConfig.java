package com.jo.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "API de Billetterie",
                version = "1.0",
                description = "API pour la gestion de la billetterie des JO 2024 de Paris",
                contact = @io.swagger.v3.oas.annotations.info.Contact(
                        name = "Support Technique",
                        email = "mr.teisseire@gmail.com"
                )
        ),
        servers = {
                @Server(
                        url = "/",
                        description = "Serveur par défaut"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Authentification JWT Bearer. Préfixez votre token avec 'Bearer '"
)
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
