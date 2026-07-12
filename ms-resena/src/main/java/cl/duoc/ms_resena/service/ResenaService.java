package cl.duoc.ms_resena.service;

import cl.duoc.ms_resena.dto.ResenaRequestDTO;
import cl.duoc.ms_resena.model.Resena;
import cl.duoc.ms_resena.repository.ResenaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;

    public ResenaService(ResenaRepository resenaRepository) {
        this.resenaRepository = resenaRepository;
    }

    /** Lista todas las reseñas (para uso administrativo). */
    public List<Resena> listarTodas() {
        return resenaRepository.findAll();
    }

    /** Busca una reseña por ID. */
    public Resena buscarPorId(Long id) {
        return resenaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reseña no encontrada con id: " + id));
    }

    /** Devuelve solo las reseñas visibles de un producto. */
    public List<Resena> listarPorProducto(Long productoId) {
        return resenaRepository.findByProductoIdAndVisible(productoId, true);
    }

    /** Devuelve todas las reseñas de un cliente. */
    public List<Resena> listarPorCliente(Long clienteId) {
        return resenaRepository.findByClienteId(clienteId);
    }

    /**
     * Crea una nueva reseña.
     * Regla de negocio: un cliente solo puede reseñar un producto una vez.
     */
    @Transactional
    public Resena crear(ResenaRequestDTO dto) {
        if (resenaRepository.existsByClienteIdAndProductoId(dto.getClienteId(), dto.getProductoId())) {
            throw new IllegalStateException(
                    "El cliente " + dto.getClienteId() +
                    " ya ha reseñado el producto " + dto.getProductoId());
        }

        Resena resena = Resena.builder()
                .clienteId(dto.getClienteId())
                .productoId(dto.getProductoId())
                .puntuacion(dto.getPuntuacion())
                .comentario(dto.getComentario())
                .build();

        return resenaRepository.save(resena);
    }

    /**
     * Actualiza el comentario y la puntuación de una reseña existente.
     * Solo se permiten editar comentario y puntuación, no cambiar producto/cliente.
     */
    @Transactional
    public Resena actualizar(Long id, ResenaRequestDTO dto) {
        Resena resena = buscarPorId(id);

        if (!resena.getVisible()) {
            throw new IllegalStateException("No se puede editar una reseña que ha sido ocultada por moderación");
        }

        resena.setPuntuacion(dto.getPuntuacion());
        resena.setComentario(dto.getComentario());
        return resenaRepository.save(resena);
    }

    /**
     * Oculta la reseña (moderación) sin eliminarla de la base de datos.
     */
    @Transactional
    public Resena ocultar(Long id) {
        Resena resena = buscarPorId(id);
        resena.setVisible(false);
        return resenaRepository.save(resena);
    }

    /** Elimina la reseña permanentemente. */
    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);
        resenaRepository.deleteById(id);
    }
}
