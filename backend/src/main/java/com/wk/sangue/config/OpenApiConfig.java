package com.wk.sangue.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI para documentar e testar as rotas da API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WK Banco de Sangue API")
                        .version("1.0.0")
                        .description("API REST para processamento de candidatos a doadores e relatórios estatísticos de saúde e compatibilidade sanguínea.")
                        .contact(new Contact()
                                .name("WK Technology")
                                .url("https://www.wktechnology.com.br")));
    }
}
