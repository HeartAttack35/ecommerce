package cl.duoc.ms_resena.controller;

import cl.duoc.ms_resena.dto.ResenaRequestDTO;
import cl.duoc.ms_resena.model.Resena;
import cl.duoc.ms_resena.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping
    public ResponseEntity<List<Resena>> listarTodas() {
        return ResponseEntity.ok(resenaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resena> obtenerResena(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.buscarPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Resena>> porProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.listarPorProducto(productoId));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Resena>> porCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(resenaService.listarPorCliente(clienteId));
    }

    @PostMapping
    public ResponseEntity<Resena> crear(@Valid @RequestBody ResenaRequestDTO dto) {
        Resena resena = resenaService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(resena.getId()).toUri();
        return ResponseEntity.created(location).body(resena);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resena> actualizar(@PathVariable Long id,
                                             @Valid @RequestBody ResenaRequestDTO dto) {
        return ResponseEntity.ok(resenaService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/ocultar")
    public ResponseEntity<Resena> ocultar(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.ocultar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
