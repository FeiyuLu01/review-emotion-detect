package org.emotion.detect.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Swagger/OpenAPI documentation
 * This sets up the API docs that you can see in the browser
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates the OpenAPI configuration bean
     * This defines how our API documentation looks
     */
    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Emotion Analysis Backend APIs")
                        .description("REST APIs for emotion analysis project")
                        .version("v1")
                        .contact(new Contact().name("Team TP03"))
                        .license(new License().name("MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("OpenAPI Docs")
                        .url("https://swagger.io/specification/"));
    }
}


