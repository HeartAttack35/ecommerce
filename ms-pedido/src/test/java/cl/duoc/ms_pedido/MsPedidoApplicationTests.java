package cl.duoc.ms_pedido;

import org.junit.jupiter.api.Test;

// Test de arranque de contexto deshabilitado intencionalmente:
// requiere BD MySQL y Eureka en ejecución, lo que está fuera del alcance
// de las pruebas unitarias. Las pruebas de servicio están en el paquete 'service'.
class MsPedidoApplicationTests {

    @Test
    void placeholder() {
        // Sin contexto de Spring: las pruebas reales están en PedidoServiceTest
    }
}
