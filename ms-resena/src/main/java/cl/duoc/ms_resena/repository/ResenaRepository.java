package cl.duoc.ms_resena.repository;

import cl.duoc.ms_resena.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    /** Todas las reseñas visibles de un producto. */
    List<Resena> findByProductoIdAndVisible(Long productoId, Boolean visible);

    /** Todas las reseñas (visibles e invisibles) de un cliente. */
    List<Resena> findByClienteId(Long clienteId);

    /** Verifica si un cliente ya reseñó un producto. */
    boolean existsByClienteIdAndProductoId(Long clienteId, Long productoId);
}
