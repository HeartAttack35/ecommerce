package cl.duoc.ms_promocion.controller;

import cl.duoc.ms_promocion.dto.PromocionRequestDTO;
import cl.duoc.ms_promocion.model.Promocion;
import cl.duoc.ms_promocion.service.PromocionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/promociones")
@Tag(name = "Promociones", description = "Gestión de códigos de descuento y su validación de vigencia")
public class PromocionController {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las promociones",
               description = "Devuelve todas las promociones registradas con enlaces HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Promocion>>> listarTodas() {
        List<EntityModel<Promocion>> recursos = promocionService.listarTodas().stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(this.getClass()).obtenerPromocion(p.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodas()).withRel("promociones")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodas()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener promoción por ID",
               description = "Retorna los datos de una promoción específica")
    public ResponseEntity<EntityModel<Promocion>> obtenerPromocion(@PathVariable Long id) {
        Promocion promo = promocionService.buscarPorId(id);
        EntityModel<Promocion> recurso = EntityModel.of(promo,
                linkTo(methodOn(this.getClass()).obtenerPromocion(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodas()).withRel("promociones"));
        return ResponseEntity.ok(recurso);
    }

    @GetMapping("/validar/{codigo}")
    @Operation(summary = "Validar código de promoción",
               description = "Verifica que el código exista, esté activo y dentro de su período de vigencia")
    public ResponseEntity<EntityModel<Promocion>> validar(@PathVariable String codigo) {
        Promocion promo = promocionService.validarCodigo(codigo);
        EntityModel<Promocion> recurso = EntityModel.of(promo,
                linkTo(methodOn(this.getClass()).obtenerPromocion(promo.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodas()).withRel("promociones"));
        return ResponseEntity.ok(recurso);
    }

    @PostMapping
    @Operation(summary = "Crear promoción",
               description = "Registra un nuevo código de descuento con su rango de fechas de vigencia")
    public ResponseEntity<EntityModel<Promocion>> crear(@Valid @RequestBody PromocionRequestDTO dto) {
        Promocion promo = promocionService.crear(dto);
        EntityModel<Promocion> recurso = EntityModel.of(promo,
                linkTo(methodOn(this.getClass()).obtenerPromocion(promo.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodas()).withRel("promociones"));
        return ResponseEntity.created(
                linkTo(methodOn(this.getClass()).obtenerPromocion(promo.getId())).toUri()
        ).body(recurso);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar promoción",
               description = "Reemplaza todos los datos de una promoción existente")
    public ResponseEntity<EntityModel<Promocion>> actualizar(@PathVariable Long id,
                                                              @Valid @RequestBody PromocionRequestDTO dto) {
        Promocion promo = promocionService.actualizar(id, dto);
        EntityModel<Promocion> recurso = EntityModel.of(promo,
                linkTo(methodOn(this.getClass()).obtenerPromocion(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodas()).withRel("promociones"));
        return ResponseEntity.ok(recurso);
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar promoción",
               description = "Desactiva la promoción sin eliminarla del sistema")
    public ResponseEntity<EntityModel<Promocion>> desactivar(@PathVariable Long id) {
        Promocion promo = promocionService.desactivar(id);
        EntityModel<Promocion> recurso = EntityModel.of(promo,
                linkTo(methodOn(this.getClass()).obtenerPromocion(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodas()).withRel("promociones"));
        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar promoción",
               description = "Elimina permanentemente la promoción del sistema")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        promocionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
