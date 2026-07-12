package cl.duoc.ms_resena.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Reseñas")
                        .version("1.0.0")
                        .description("Microservicio responsable de la gestión de reseñas y puntuaciones: " +
                                "creación, moderación y consulta de opiniones de productos."));
    }
}
