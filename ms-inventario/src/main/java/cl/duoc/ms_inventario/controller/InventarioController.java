package cl.duoc.ms_inventario.controller;

import cl.duoc.ms_inventario.dto.InventarioRequestDTO;
import cl.duoc.ms_inventario.model.Inventario;
import cl.duoc.ms_inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public ResponseEntity<List<Inventario>> listarTodos() {
        return ResponseEntity.ok(inventarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerInventario(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.buscarPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Inventario> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.buscarPorProducto(productoId));
    }

    @GetMapping("/producto/{productoId}/stock")
    public ResponseEntity<Integer> obtenerStockPorProducto(@PathVariable Long productoId) {
        Inventario inventario = inventarioService.buscarPorProducto(productoId);
        return ResponseEntity.ok(inventario.getCantidadDisponible());
    }

    @PostMapping
    public ResponseEntity<Inventario> crear(@Valid @RequestBody InventarioRequestDTO dto) {
        Inventario inventario = inventarioService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(inventario.getId()).toUri();
        return ResponseEntity.created(location).body(inventario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizar(@PathVariable Long id,
                                                 @Valid @RequestBody InventarioRequestDTO dto) {
        return ResponseEntity.ok(inventarioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
