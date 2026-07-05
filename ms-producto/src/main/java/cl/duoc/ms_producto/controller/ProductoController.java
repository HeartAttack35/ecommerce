package cl.duoc.ms_producto.controller;

import cl.duoc.ms_producto.dto.ProductoRequestDTO;
import cl.duoc.ms_producto.model.Producto;
import cl.duoc.ms_producto.service.ProductoService;
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
@RequestMapping("/productos")
@Tag(name = "Productos", description = "Operaciones para la gestión del catálogo de productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Retorna el catálogo completo de productos con enlaces HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> listarTodos() {
        List<EntityModel<Producto>> recursos = productoService.listarTodos().stream()
                .map(producto -> EntityModel.of(producto,
                        linkTo(methodOn(this.getClass()).obtenerProducto(producto.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodos()).withRel("productos")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Retorna los datos de un producto específico según su identificador")
    public ResponseEntity<EntityModel<Producto>> obtenerProducto(@PathVariable Long id) {
        Producto producto = productoService.buscarPorId(id);
        EntityModel<Producto> recurso = EntityModel.of(producto,
                linkTo(methodOn(this.getClass()).obtenerProducto(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("productos"));
        return ResponseEntity.ok(recurso);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo producto", description = "Registra un nuevo producto en el catálogo del ecommerce")
    public ResponseEntity<EntityModel<Producto>> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        Producto producto = productoService.crear(dto);
        EntityModel<Producto> recurso = EntityModel.of(producto,
                linkTo(methodOn(this.getClass()).obtenerProducto(producto.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("productos"));
        return ResponseEntity.created(
                linkTo(methodOn(this.getClass()).obtenerProducto(producto.getId())).toUri()
        ).body(recurso);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Reemplaza los datos de un producto existente por completo")
    public ResponseEntity<EntityModel<Producto>> actualizar(@PathVariable Long id,
                                                             @Valid @RequestBody ProductoRequestDTO dto) {
        Producto producto = productoService.actualizar(id, dto);
        EntityModel<Producto> recurso = EntityModel.of(producto,
                linkTo(methodOn(this.getClass()).obtenerProducto(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("productos"));
        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina permanentemente un producto del catálogo")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
