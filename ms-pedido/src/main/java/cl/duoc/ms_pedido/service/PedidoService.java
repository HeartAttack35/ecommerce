package cl.duoc.ms_pedido.service;

import cl.duoc.ms_pedido.client.InventarioClient;
import cl.duoc.ms_pedido.dto.PedidoRequestDTO;
import cl.duoc.ms_pedido.model.Pedido;
import cl.duoc.ms_pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final InventarioClient inventarioClient;

    public PedidoService(PedidoRepository pedidoRepository, InventarioClient inventarioClient) {
        this.pedidoRepository = pedidoRepository;
        this.inventarioClient = inventarioClient;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado con id: " + id));
    }

    public List<Pedido> buscarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Transactional
    public Pedido crear(PedidoRequestDTO dto) {
        // 1. Llamada síncrona a ms-inventario vía Feign.
        //    El FeignClientInterceptor propaga automáticamente el JWT en el header Authorization.
        Integer stockDisponible = inventarioClient.obtenerStockPorProducto(dto.getProductoId());

        // 2. Validación de negocio: rechazar si no hay stock suficiente
        if (stockDisponible == null || stockDisponible < dto.getCantidad()) {
            throw new IllegalArgumentException(
                    "Stock insuficiente en el inventario para el producto solicitado. " +
                    "Disponible: " + stockDisponible + ", Solicitado: " + dto.getCantidad());
        }

        // 3. Flujo normal: guardar el pedido
        Pedido pedido = Pedido.builder()
                .clienteId(dto.getClienteId())
                .productoId(dto.getProductoId())
                .cantidad(dto.getCantidad())
                .total(BigDecimal.ZERO)
                .estado("PENDIENTE")
                .fechaPedido(LocalDateTime.now())
                .build();

        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarEstado(Long id, String nuevoEstado) {
        Pedido pedido = buscarPorId(id);
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        pedidoRepository.deleteById(id);
    }
}
