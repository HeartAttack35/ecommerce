package cl.duoc.ms_promocion.controller;

import cl.duoc.ms_promocion.dto.PromocionRequestDTO;
import cl.duoc.ms_promocion.model.Promocion;
import cl.duoc.ms_promocion.service.PromocionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/promociones")
public class PromocionController {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @GetMapping
    public ResponseEntity<List<Promocion>> listarTodas() {
        return ResponseEntity.ok(promocionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promocion> obtenerPromocion(@PathVariable Long id) {
        return ResponseEntity.ok(promocionService.buscarPorId(id));
    }

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<Promocion> validar(@PathVariable String codigo) {
        return ResponseEntity.ok(promocionService.validarCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<Promocion> crear(@Valid @RequestBody PromocionRequestDTO dto) {
        Promocion promo = promocionService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(promo.getId()).toUri();
        return ResponseEntity.created(location).body(promo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promocion> actualizar(@PathVariable Long id,
                                                @Valid @RequestBody PromocionRequestDTO dto) {
        return ResponseEntity.ok(promocionService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Promocion> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(promocionService.desactivar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        promocionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
