package cl.duoc.ms_promocion.service;

import cl.duoc.ms_promocion.dto.PromocionRequestDTO;
import cl.duoc.ms_promocion.model.Promocion;
import cl.duoc.ms_promocion.repository.PromocionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PromocionService {

    private final PromocionRepository promocionRepository;

    public PromocionService(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    public List<Promocion> listarTodas() {
        return promocionRepository.findAll();
    }

    public Promocion buscarPorId(Long id) {
        return promocionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promoción no encontrada con id: " + id));
    }

    /**
     * Valida y devuelve una promoción a partir de su código.
     * Comprueba que:
     *  1. El código exista.
     *  2. La promoción esté activa.
     *  3. La fecha actual esté dentro del rango de vigencia.
     */
    public Promocion validarCodigo(String codigo) {
        Promocion promo = promocionRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Código de promoción no encontrado: " + codigo));

        if (!promo.getActiva()) {
            throw new IllegalStateException("La promoción '" + codigo + "' está desactivada");
        }

        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(promo.getFechaInicio())) {
            throw new IllegalStateException("La promoción '" + codigo + "' aún no ha comenzado");
        }
        if (hoy.isAfter(promo.getFechaFin())) {
            throw new IllegalStateException("La promoción '" + codigo + "' ha expirado");
        }

        return promo;
    }

    @Transactional
    public Promocion crear(PromocionRequestDTO dto) {
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        if (promocionRepository.existsByCodigo(dto.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una promoción con el código: " + dto.getCodigo());
        }

        Promocion promo = Promocion.builder()
                .codigo(dto.getCodigo().toUpperCase())
                .descripcion(dto.getDescripcion())
                .porcentajeDescuento(dto.getPorcentajeDescuento())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .build();

        return promocionRepository.save(promo);
    }

    @Transactional
    public Promocion actualizar(Long id, PromocionRequestDTO dto) {
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        Promocion promo = buscarPorId(id);
        promo.setCodigo(dto.getCodigo().toUpperCase());
        promo.setDescripcion(dto.getDescripcion());
        promo.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        promo.setFechaInicio(dto.getFechaInicio());
        promo.setFechaFin(dto.getFechaFin());
        return promocionRepository.save(promo);
    }

    /** Desactiva la promoción sin eliminarla. */
    @Transactional
    public Promocion desactivar(Long id) {
        Promocion promo = buscarPorId(id);
        promo.setActiva(false);
        return promocionRepository.save(promo);
    }

    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);
        promocionRepository.deleteById(id);
    }
}
