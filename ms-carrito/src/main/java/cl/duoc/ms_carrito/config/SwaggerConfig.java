package cl.duoc.ms_carrito.config;

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
                        .title("API de Carrito de Compras")
                        .version("1.0.0")
                        .description("Microservicio responsable de la gestión del carrito de compras: " +
                                "creación, adición de ítems y confirmación antes de generar un pedido."));
    }
}
