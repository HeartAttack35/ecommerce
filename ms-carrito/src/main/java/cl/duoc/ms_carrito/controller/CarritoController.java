package cl.duoc.ms_carrito.controller;

import cl.duoc.ms_carrito.dto.CarritoRequestDTO;
import cl.duoc.ms_carrito.dto.ItemCarritoDTO;
import cl.duoc.ms_carrito.model.Carrito;
import cl.duoc.ms_carrito.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<List<Carrito>> listarTodos() {
        return ResponseEntity.ok(carritoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerCarrito(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Carrito>> porCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(carritoService.buscarPorCliente(clienteId));
    }

    @PostMapping
    public ResponseEntity<Carrito> crear(@Valid @RequestBody CarritoRequestDTO dto) {
        Carrito carrito = carritoService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(carrito.getId()).toUri();
        return ResponseEntity.created(location).body(carrito);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<Carrito> agregarItems(
            @PathVariable Long id,
            @Valid @RequestBody List<@Valid ItemCarritoDTO> items) {
        return ResponseEntity.ok(carritoService.agregarItems(id, items));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Carrito> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.confirmar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        carritoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
