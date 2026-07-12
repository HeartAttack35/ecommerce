package cl.duoc.ms_carrito.service;

import cl.duoc.ms_carrito.dto.CarritoRequestDTO;
import cl.duoc.ms_carrito.dto.ItemCarritoDTO;
import cl.duoc.ms_carrito.model.Carrito;
import cl.duoc.ms_carrito.model.ItemCarrito;
import cl.duoc.ms_carrito.repository.CarritoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;

    public CarritoService(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    /** Devuelve todos los carritos. */
    public List<Carrito> listarTodos() {
        return carritoRepository.findAll();
    }

    /** Busca un carrito por ID. */
    public Carrito buscarPorId(Long id) {
        return carritoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado con id: " + id));
    }

    /** Devuelve todos los carritos de un cliente. */
    public List<Carrito> buscarPorCliente(Long clienteId) {
        return carritoRepository.findByClienteId(clienteId);
    }

    /**
     * Crea un nuevo carrito ACTIVO para el cliente.
     * Si ya tiene un carrito activo, lanza excepción para evitar duplicados.
     */
    @Transactional
    public Carrito crear(CarritoRequestDTO dto) {
        carritoRepository.findByClienteIdAndEstado(dto.getClienteId(), "ACTIVO")
                .ifPresent(c -> {
                    throw new IllegalStateException(
                            "El cliente ya tiene un carrito activo (id: " + c.getId() + "). Confírmalo o vacíalo antes de crear uno nuevo.");
                });

        List<ItemCarrito> items = dto.getItems().stream()
                .map(this::mapearItem)
                .toList();

        Carrito carrito = Carrito.builder()
                .clienteId(dto.getClienteId())
                .items(items)
                .build();

        return carritoRepository.save(carrito);
    }

    /**
     * Agrega ítems al carrito activo del cliente.
     * Si no existe carrito activo lanza excepción.
     */
    @Transactional
    public Carrito agregarItems(Long id, List<ItemCarritoDTO> nuevosItems) {
        Carrito carrito = buscarPorId(id);
        if (!"ACTIVO".equals(carrito.getEstado())) {
            throw new IllegalStateException("Solo se pueden agregar ítems a un carrito en estado ACTIVO");
        }
        nuevosItems.stream()
                .map(this::mapearItem)
                .forEach(carrito.getItems()::add);
        return carritoRepository.save(carrito);
    }

    /**
     * Confirma el carrito (cambia estado a CONFIRMADO).
     * Valida que tenga al menos un ítem antes de confirmar.
     */
    @Transactional
    public Carrito confirmar(Long id) {
        Carrito carrito = buscarPorId(id);
        if (!"ACTIVO".equals(carrito.getEstado())) {
            throw new IllegalStateException("Solo se puede confirmar un carrito en estado ACTIVO");
        }
        if (carrito.getItems().isEmpty()) {
            throw new IllegalStateException("No se puede confirmar un carrito vacío");
        }
        carrito.setEstado("CONFIRMADO");
        return carritoRepository.save(carrito);
    }

    /** Elimina el carrito por ID. */
    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);
        carritoRepository.deleteById(id);
    }

    // ─── helpers ───────────────────────────────────────────────────────────────

    private ItemCarrito mapearItem(ItemCarritoDTO dto) {
        return ItemCarrito.builder()
                .productoId(dto.getProductoId())
                .nombreProducto(dto.getNombreProducto())
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .build();
    }
}
