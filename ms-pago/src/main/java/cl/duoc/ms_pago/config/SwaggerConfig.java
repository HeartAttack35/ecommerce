package cl.duoc.ms_pago.config;

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
                        .title("API de Gestión de Pagos")
                        .version("1.0.0")
                        .description("Microservicio responsable del procesamiento y seguimiento de pagos asociados a pedidos."));
    }
}
