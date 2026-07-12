package cl.duoc.ms_resena.controller;

import cl.duoc.ms_resena.dto.ResenaRequestDTO;
import cl.duoc.ms_resena.model.Resena;
import cl.duoc.ms_resena.service.ResenaService;
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
@RequestMapping("/resenas")
@Tag(name = "Reseñas", description = "Gestión de reseñas y puntuaciones de productos del ecommerce")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las reseñas",
               description = "Devuelve todas las reseñas registradas (uso administrativo) con enlaces HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Resena>>> listarTodas() {
        List<EntityModel<Resena>> recursos = resenaService.listarTodas().stream()
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(this.getClass()).obtenerResena(r.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodas()).withRel("resenas")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodas()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reseña por ID",
               description = "Retorna el detalle de una reseña específica")
    public ResponseEntity<EntityModel<Resena>> obtenerResena(@PathVariable Long id) {
        Resena resena = resenaService.buscarPorId(id);
        EntityModel<Resena> recurso = EntityModel.of(resena,
                linkTo(methodOn(this.getClass()).obtenerResena(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodas()).withRel("resenas"));
        return ResponseEntity.ok(recurso);
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Listar reseñas visibles de un producto",
               description = "Devuelve únicamente las reseñas visibles (no moderadas) de un producto")
    public ResponseEntity<CollectionModel<EntityModel<Resena>>> porProducto(@PathVariable Long productoId) {
        List<EntityModel<Resena>> recursos = resenaService.listarPorProducto(productoId).stream()
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(this.getClass()).obtenerResena(r.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).porProducto(productoId)).withRel("producto-resenas")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).porProducto(productoId)).withSelfRel()));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar reseñas de un cliente",
               description = "Devuelve todas las reseñas escritas por un cliente (incluyendo las ocultas)")
    public ResponseEntity<CollectionModel<EntityModel<Resena>>> porCliente(@PathVariable Long clienteId) {
        List<EntityModel<Resena>> recursos = resenaService.listarPorCliente(clienteId).stream()
                .map(r -> EntityModel.of(r,
                        linkTo(methodOn(this.getClass()).obtenerResena(r.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).porCliente(clienteId)).withRel("cliente-resenas")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).porCliente(clienteId)).withSelfRel()));
    }

    @PostMapping
    @Operation(summary = "Crear reseña",
               description = "Registra una nueva reseña. Un cliente solo puede reseñar cada producto una vez.")
    public ResponseEntity<EntityModel<Resena>> crear(@Valid @RequestBody ResenaRequestDTO dto) {
        Resena resena = resenaService.crear(dto);
        EntityModel<Resena> recurso = EntityModel.of(resena,
                linkTo(methodOn(this.getClass()).obtenerResena(resena.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodas()).withRel("resenas"),
                linkTo(methodOn(this.getClass()).porProducto(resena.getProductoId())).withRel("producto-resenas"));
        return ResponseEntity.created(
                linkTo(methodOn(this.getClass()).obtenerResena(resena.getId())).toUri()
        ).body(recurso);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reseña",
               description = "Permite al autor corregir su puntuación y comentario")
    public ResponseEntity<EntityModel<Resena>> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody ResenaRequestDTO dto) {
        Resena resena = resenaService.actualizar(id, dto);
        EntityModel<Resena> recurso = EntityModel.of(resena,
                linkTo(methodOn(this.getClass()).obtenerResena(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodas()).withRel("resenas"));
        return ResponseEntity.ok(recurso);
    }

    @PatchMapping("/{id}/ocultar")
    @Operation(summary = "Ocultar reseña (moderación)",
               description = "Marca la reseña como no visible sin eliminarla de la base de datos")
    public ResponseEntity<EntityModel<Resena>> ocultar(@PathVariable Long id) {
        Resena resena = resenaService.ocultar(id);
        EntityModel<Resena> recurso = EntityModel.of(resena,
                linkTo(methodOn(this.getClass()).obtenerResena(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodas()).withRel("resenas"));
        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reseña",
               description = "Elimina permanentemente la reseña del sistema")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
