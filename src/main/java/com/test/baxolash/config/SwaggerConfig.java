package com.test.baxolash.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI baxolashOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Baholash Admin API")
                        .description("API административной панели Baholash")
                        .version("1.0.0"));
    }
}

