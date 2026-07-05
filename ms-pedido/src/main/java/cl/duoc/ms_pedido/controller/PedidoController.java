package cl.duoc.ms_pedido.controller;

import cl.duoc.ms_pedido.dto.PedidoRequestDTO;
import cl.duoc.ms_pedido.model.Pedido;
import cl.duoc.ms_pedido.service.PedidoService;
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
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Operaciones para la creación y seguimiento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos", description = "Retorna la lista completa de pedidos registrados en el sistema")
    public ResponseEntity<CollectionModel<EntityModel<Pedido>>> listarTodos() {
        List<EntityModel<Pedido>> recursos = pedidoService.listarTodos().stream()
                .map(pedido -> EntityModel.of(pedido,
                        linkTo(methodOn(this.getClass()).obtenerPedido(pedido.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodos()).withRel("pedidos")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID", description = "Retorna los datos de un pedido específico según su identificador")
    public ResponseEntity<EntityModel<Pedido>> obtenerPedido(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        EntityModel<Pedido> recurso = EntityModel.of(pedido,
                linkTo(methodOn(this.getClass()).obtenerPedido(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("pedidos"));
        return ResponseEntity.ok(recurso);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener pedidos por cliente", description = "Retorna todos los pedidos realizados por un cliente específico")
    public ResponseEntity<CollectionModel<EntityModel<Pedido>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<EntityModel<Pedido>> recursos = pedidoService.buscarPorCliente(clienteId).stream()
                .map(pedido -> EntityModel.of(pedido,
                        linkTo(methodOn(this.getClass()).obtenerPedido(pedido.getId())).withSelfRel()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).obtenerPorCliente(clienteId)).withSelfRel()));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo pedido", description = "Registra un nuevo pedido con estado PENDIENTE para el cliente indicado")
    public ResponseEntity<EntityModel<Pedido>> crear(@Valid @RequestBody PedidoRequestDTO dto) {
        Pedido pedido = pedidoService.crear(dto);
        EntityModel<Pedido> recurso = EntityModel.of(pedido,
                linkTo(methodOn(this.getClass()).obtenerPedido(pedido.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("pedidos"));
        return ResponseEntity.created(
                linkTo(methodOn(this.getClass()).obtenerPedido(pedido.getId())).toUri()
        ).body(recurso);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del pedido", description = "Cambia el estado del pedido (PENDIENTE, CONFIRMADO, CANCELADO, ENTREGADO)")
    public ResponseEntity<EntityModel<Pedido>> actualizarEstado(@PathVariable Long id,
                                                                 @RequestParam String estado) {
        Pedido pedido = pedidoService.actualizarEstado(id, estado);
        EntityModel<Pedido> recurso = EntityModel.of(pedido,
                linkTo(methodOn(this.getClass()).obtenerPedido(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("pedidos"));
        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pedido", description = "Elimina permanentemente un pedido del sistema")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
