package com.example.weather.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = true)
public class OpenApiConfig {

    @Value("${api.info.title: api.info.title}")
    private String title;

    @Value("${api.info.description: api.info.description}")
    private String description;

    @Value("${api.info.version: api.info.version}")
    private String version;

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(info());
    }

    private Info info() {
        return new Info()
                .title(title)
                .description(description)
                .version(version);
    }

}
