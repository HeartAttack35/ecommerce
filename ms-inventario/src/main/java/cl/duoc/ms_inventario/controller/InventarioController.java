package cl.duoc.ms_inventario.controller;

import cl.duoc.ms_inventario.dto.InventarioRequestDTO;
import cl.duoc.ms_inventario.model.Inventario;
import cl.duoc.ms_inventario.service.InventarioService;
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
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Inventario>>> listarTodos() {
        List<EntityModel<Inventario>> recursos = inventarioService.listarTodos().stream()
                .map(inv -> EntityModel.of(inv,
                        linkTo(methodOn(this.getClass()).obtenerInventario(inv.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodos()).withRel("inventario")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Inventario>> coleccion = CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodos()).withSelfRel());

        return ResponseEntity.ok(coleccion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> obtenerInventario(@PathVariable Long id) {
        Inventario inventario = inventarioService.buscarPorId(id);

        EntityModel<Inventario> recurso = EntityModel.of(inventario,
                linkTo(methodOn(this.getClass()).obtenerInventario(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("inventario"));

        return ResponseEntity.ok(recurso);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<EntityModel<Inventario>> obtenerPorProducto(@PathVariable Long productoId) {
        Inventario inventario = inventarioService.buscarPorProducto(productoId);

        EntityModel<Inventario> recurso = EntityModel.of(inventario,
                linkTo(methodOn(this.getClass()).obtenerInventario(inventario.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("inventario"));

        return ResponseEntity.ok(recurso);
    }

    @PostMapping
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
    public ResponseEntity<EntityModel<Inventario>> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody InventarioRequestDTO dto) {
        Inventario inventario = inventarioService.actualizar(id, dto);

        EntityModel<Inventario> recurso = EntityModel.of(inventario,
                linkTo(methodOn(this.getClass()).obtenerInventario(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("inventario"));

        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
