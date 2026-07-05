package cl.duoc.ms_pedido.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Interceptor global para todos los clientes Feign de ms-pedido.
 * Extrae el header "Authorization" de la petición HTTP entrante y lo propaga
 * automáticamente en cada llamada saliente hacia otros microservicios,
 * cumpliendo el modelo de seguridad Zero Trust.
 */
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 1. Obtener el contexto de la petición HTTP entrante actual a ms-pedido
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // 2. Extraer el header "Authorization" que contiene el JWT del usuario
            String authorizationHeader = request.getHeader("Authorization");

            // 3. Si existe, adjuntarlo a la petición saliente hacia ms-inventario
            if (authorizationHeader != null) {
                template.header("Authorization", authorizationHeader);
            }
        }
    }
}
