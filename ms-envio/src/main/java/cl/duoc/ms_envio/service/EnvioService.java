package cl.duoc.ms_envio.service;

import cl.duoc.ms_envio.dto.EnvioRequestDTO;
import cl.duoc.ms_envio.model.Envio;
import cl.duoc.ms_envio.repository.EnvioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;

    public EnvioService(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    public List<Envio> listarTodos() {
        return envioRepository.findAll();
    }

    public Envio buscarPorId(Long id) {
        return envioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Envío no encontrado con id: " + id));
    }

    public Envio buscarPorPedido(Long pedidoId) {
        return envioRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new NoSuchElementException("Envío no encontrado para el pedido: " + pedidoId));
    }

    public Envio crear(EnvioRequestDTO dto) {
        Envio envio = Envio.builder()
                .pedidoId(dto.getPedidoId())
                .direccionDestino(dto.getDireccionDestino())
                .transportista(dto.getTransportista())
                .estado("PREPARANDO")
                .numeroSeguimiento(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .fechaEstimadaEntrega(dto.getFechaEstimadaEntrega())
                .build();
        return envioRepository.save(envio);
    }

    public Envio actualizarEstado(Long id, String nuevoEstado) {
        Envio envio = buscarPorId(id);
        envio.setEstado(nuevoEstado);
        return envioRepository.save(envio);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        envioRepository.deleteById(id);
    }
}
