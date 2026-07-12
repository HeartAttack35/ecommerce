package cl.duoc.ms_carrito.controller;

import cl.duoc.ms_carrito.dto.CarritoRequestDTO;
import cl.duoc.ms_carrito.dto.ItemCarritoDTO;
import cl.duoc.ms_carrito.model.Carrito;
import cl.duoc.ms_carrito.service.CarritoService;
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
@RequestMapping("/carritos")
@Tag(name = "Carritos", description = "Gestión del carrito de compras del ecommerce")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los carritos",
               description = "Devuelve todos los carritos registrados con enlaces HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Carrito>>> listarTodos() {
        List<EntityModel<Carrito>> recursos = carritoService.listarTodos().stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(this.getClass()).obtenerCarrito(c.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodos()).withRel("carritos")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener carrito por ID",
               description = "Retorna el carrito y sus ítems para el ID indicado")
    public ResponseEntity<EntityModel<Carrito>> obtenerCarrito(@PathVariable Long id) {
        Carrito carrito = carritoService.buscarPorId(id);
        EntityModel<Carrito> recurso = EntityModel.of(carrito,
                linkTo(methodOn(this.getClass()).obtenerCarrito(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("carritos"));
        return ResponseEntity.ok(recurso);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar carritos de un cliente",
               description = "Devuelve todos los carritos (en cualquier estado) de un cliente")
    public ResponseEntity<CollectionModel<EntityModel<Carrito>>> porCliente(@PathVariable Long clienteId) {
        List<EntityModel<Carrito>> recursos = carritoService.buscarPorCliente(clienteId).stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(this.getClass()).obtenerCarrito(c.getId())).withSelfRel()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).porCliente(clienteId)).withSelfRel()));
    }

    @PostMapping
    @Operation(summary = "Crear un carrito",
               description = "Crea un carrito ACTIVO para el cliente. Falla si ya tiene uno activo.")
    public ResponseEntity<EntityModel<Carrito>> crear(@Valid @RequestBody CarritoRequestDTO dto) {
        Carrito carrito = carritoService.crear(dto);
        EntityModel<Carrito> recurso = EntityModel.of(carrito,
                linkTo(methodOn(this.getClass()).obtenerCarrito(carrito.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("carritos"));
        return ResponseEntity.created(
                linkTo(methodOn(this.getClass()).obtenerCarrito(carrito.getId())).toUri()
        ).body(recurso);
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Agregar ítems al carrito",
               description = "Añade uno o más productos al carrito activo indicado")
    public ResponseEntity<EntityModel<Carrito>> agregarItems(
            @PathVariable Long id,
            @Valid @RequestBody List<@Valid ItemCarritoDTO> items) {
        Carrito carrito = carritoService.agregarItems(id, items);
        EntityModel<Carrito> recurso = EntityModel.of(carrito,
                linkTo(methodOn(this.getClass()).obtenerCarrito(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("carritos"));
        return ResponseEntity.ok(recurso);
    }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar carrito",
               description = "Cambia el estado del carrito a CONFIRMADO, listo para generar un pedido")
    public ResponseEntity<EntityModel<Carrito>> confirmar(@PathVariable Long id) {
        Carrito carrito = carritoService.confirmar(id);
        EntityModel<Carrito> recurso = EntityModel.of(carrito,
                linkTo(methodOn(this.getClass()).obtenerCarrito(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("carritos"));
        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar carrito",
               description = "Elimina permanentemente el carrito y todos sus ítems")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        carritoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
