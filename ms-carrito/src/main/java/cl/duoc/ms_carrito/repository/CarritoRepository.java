package cl.duoc.ms_carrito.repository;

import cl.duoc.ms_carrito.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    /** Busca el carrito activo de un cliente. */
    Optional<Carrito> findByClienteIdAndEstado(Long clienteId, String estado);

    /** Lista todos los carritos de un cliente. */
    List<Carrito> findByClienteId(Long clienteId);
}
