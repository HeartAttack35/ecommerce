package cl.duoc.ms_pedido.service;

import cl.duoc.ms_pedido.dto.PedidoRequestDTO;
import cl.duoc.ms_pedido.model.Pedido;
import cl.duoc.ms_pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
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

    public Pedido crear(PedidoRequestDTO dto) {
        Pedido pedido = Pedido.builder()
                .clienteId(dto.getClienteId())
                .productoId(dto.getProductoId())
                .cantidad(dto.getCantidad())
                // El total se calcula externamente o se puede extender con precio de producto
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
