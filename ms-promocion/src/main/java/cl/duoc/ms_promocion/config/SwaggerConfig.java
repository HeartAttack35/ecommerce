package cl.duoc.ms_promocion.config;

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
                        .title("API de Promociones")
                        .version("1.0.0")
                        .description("Microservicio responsable de la gestión de códigos de descuento: " +
                                "creación, validación de vigencia y desactivación de promociones."));
    }
}
