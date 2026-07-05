package cl.duoc.ms_inventario.controller;

import cl.duoc.ms_inventario.dto.InventarioRequestDTO;
import cl.duoc.ms_inventario.model.Inventario;
import cl.duoc.ms_inventario.service.InventarioService;
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
@RequestMapping("/inventario")
@Tag(name = "Inventario", description = "Operaciones para el control de stock y disponibilidad de productos")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los registros de inventario", description = "Retorna el inventario completo con cantidades disponibles y reservadas")
    public ResponseEntity<CollectionModel<EntityModel<Inventario>>> listarTodos() {
        List<EntityModel<Inventario>> recursos = inventarioService.listarTodos().stream()
                .map(inv -> EntityModel.of(inv,
                        linkTo(methodOn(this.getClass()).obtenerInventario(inv.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodos()).withRel("inventario")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro de inventario por ID", description = "Retorna los datos de un registro de inventario según su identificador")
    public ResponseEntity<EntityModel<Inventario>> obtenerInventario(@PathVariable Long id) {
        Inventario inventario = inventarioService.buscarPorId(id);
        EntityModel<Inventario> recurso = EntityModel.of(inventario,
                linkTo(methodOn(this.getClass()).obtenerInventario(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("inventario"));
        return ResponseEntity.ok(recurso);
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Obtener inventario por producto", description = "Retorna el registro de stock asociado a un producto específico")
    public ResponseEntity<EntityModel<Inventario>> obtenerPorProducto(@PathVariable Long productoId) {
        Inventario inventario = inventarioService.buscarPorProducto(productoId);
        EntityModel<Inventario> recurso = EntityModel.of(inventario,
                linkTo(methodOn(this.getClass()).obtenerInventario(inventario.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("inventario"));
        return ResponseEntity.ok(recurso);
    }

    @PostMapping
    @Operation(summary = "Crear registro de inventario", description = "Registra el stock inicial de un producto. Solo se permite un registro por producto")
    public ResponseEntity<EntityModel<Inventario>> crear(@Valid @RequestBody InventarioRequestDTO dto) {
        Inventario inventario = inventarioService.crear(dto);
        EntityModel<Inventario> recurso = EntityModel.of(inventario,
                linkTo(methodOn(this.getClass()).obtenerInventario(inventario.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("inventario"));
        return ResponseEntity.created(
                linkTo(methodOn(this.getClass()).obtenerInventario(inventario.getId())).toUri()
        ).body(recurso);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar inventario", description = "Actualiza las cantidades disponibles, reservadas y ubicación de un registro de inventario")
    public ResponseEntity<EntityModel<Inventario>> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody InventarioRequestDTO dto) {
        Inventario inventario = inventarioService.actualizar(id, dto);
        EntityModel<Inventario> recurso = EntityModel.of(inventario,
                linkTo(methodOn(this.getClass()).obtenerInventario(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("inventario"));
        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro de inventario", description = "Elimina un registro de inventario del sistema")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
