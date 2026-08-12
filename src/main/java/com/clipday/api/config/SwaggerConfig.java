package com.clipday.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI clipdayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clipday API")
                        .description("날짜별 사진·메모 기록 API")
                        .version("v1.0"));
    }
}