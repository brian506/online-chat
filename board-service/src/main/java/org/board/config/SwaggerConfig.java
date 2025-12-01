package org.board.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Board API Docs") // 문서 제목
                        .description("Board-service API 명세서입니다.") // 문서 설명
                        .version("1.0.0")); // 버전
    }
}
