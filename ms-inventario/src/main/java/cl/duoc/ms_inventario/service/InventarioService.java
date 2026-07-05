package cl.duoc.ms_inventario.service;

import cl.duoc.ms_inventario.dto.InventarioRequestDTO;
import cl.duoc.ms_inventario.model.Inventario;
import cl.duoc.ms_inventario.repository.InventarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }

    public Inventario buscarPorId(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Inventario no encontrado con id: " + id));
    }

    public Inventario buscarPorProducto(Long productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new NoSuchElementException("Inventario no encontrado para el producto: " + productoId));
    }

    public Inventario crear(InventarioRequestDTO dto) {
        if (inventarioRepository.existsByProductoId(dto.getProductoId())) {
            throw new IllegalArgumentException("Ya existe registro de inventario para el producto: " + dto.getProductoId());
        }
        Inventario inventario = Inventario.builder()
                .productoId(dto.getProductoId())
                .cantidadDisponible(dto.getCantidadDisponible())
                .cantidadReservada(dto.getCantidadReservada())
                .ubicacion(dto.getUbicacion())
                .build();
        return inventarioRepository.save(inventario);
    }

    public Inventario actualizar(Long id, InventarioRequestDTO dto) {
        Inventario inventario = buscarPorId(id);
        inventario.setCantidadDisponible(dto.getCantidadDisponible());
        inventario.setCantidadReservada(dto.getCantidadReservada());
        inventario.setUbicacion(dto.getUbicacion());
        return inventarioRepository.save(inventario);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        inventarioRepository.deleteById(id);
    }
}
