package cl.duoc.ms_notificacion.repository;

import cl.duoc.ms_notificacion.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByClienteId(Long clienteId);
    List<Notificacion> findByEstado(String estado);
    List<Notificacion> findByTipo(String tipo);
}
