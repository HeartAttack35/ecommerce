package cl.duoc.ms_pago.service;

import cl.duoc.ms_pago.dto.PagoRequestDTO;
import cl.duoc.ms_pago.model.Pago;
import cl.duoc.ms_pago.repository.PagoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    public Pago buscarPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pago no encontrado con id: " + id));
    }

    public Pago buscarPorPedido(Long pedidoId) {
        return pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new NoSuchElementException("Pago no encontrado para el pedido: " + pedidoId));
    }

    public Pago crear(PagoRequestDTO dto) {
        Pago pago = Pago.builder()
                .pedidoId(dto.getPedidoId())
                .monto(dto.getMonto())
                .metodoPago(dto.getMetodoPago())
                .estado("PENDIENTE")
                .fechaPago(LocalDateTime.now())
                .build();
        return pagoRepository.save(pago);
    }

    public Pago actualizarEstado(Long id, String nuevoEstado) {
        Pago pago = buscarPorId(id);
        pago.setEstado(nuevoEstado);
        return pagoRepository.save(pago);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        pagoRepository.deleteById(id);
    }
}
