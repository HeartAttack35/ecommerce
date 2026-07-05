package cl.duoc.ms_notificacion.service;

import cl.duoc.ms_notificacion.dto.NotificacionRequestDTO;
import cl.duoc.ms_notificacion.model.Notificacion;
import cl.duoc.ms_notificacion.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public List<Notificacion> listarTodas() {
        return notificacionRepository.findAll();
    }

    public Notificacion buscarPorId(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Notificación no encontrada con id: " + id));
    }

    public List<Notificacion> buscarPorCliente(Long clienteId) {
        return notificacionRepository.findByClienteId(clienteId);
    }

    public Notificacion enviar(NotificacionRequestDTO dto) {
        Notificacion notificacion = Notificacion.builder()
                .clienteId(dto.getClienteId())
                .tipo(dto.getTipo())
                .asunto(dto.getAsunto())
                .mensaje(dto.getMensaje())
                .estado("ENVIADO")
                .fechaEnvio(LocalDateTime.now())
                .build();
        return notificacionRepository.save(notificacion);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        notificacionRepository.deleteById(id);
    }
}
