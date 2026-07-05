package cl.duoc.ms_pedido.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign que se comunica con ms-inventario usando su nombre de registro en Eureka.
 * El interceptor FeignClientInterceptor propaga automáticamente el header Authorization.
 */
@FeignClient(name = "ms-inventario")
public interface InventarioClient {

    /**
     * Consulta la cantidad disponible de un producto en el inventario.
     * Coincide con el endpoint GET /inventario/producto/{productoId} de ms-inventario,
     * retornando solo el campo cantidadDisponible del objeto Inventario.
     */
    @GetMapping("/inventario/producto/{productoId}/stock")
    Integer obtenerStockPorProducto(@PathVariable("productoId") Long productoId);
}
