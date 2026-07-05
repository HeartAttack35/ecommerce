package cl.duoc.ms_pedido.controller;

import cl.duoc.ms_pedido.dto.PedidoRequestDTO;
import cl.duoc.ms_pedido.model.Pedido;
import cl.duoc.ms_pedido.service.PedidoService;
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
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Pedido>>> listarTodos() {
        List<EntityModel<Pedido>> recursos = pedidoService.listarTodos().stream()
                .map(pedido -> EntityModel.of(pedido,
                        linkTo(methodOn(this.getClass()).obtenerPedido(pedido.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodos()).withRel("pedidos")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Pedido>> coleccion = CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodos()).withSelfRel());

        return ResponseEntity.ok(coleccion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Pedido>> obtenerPedido(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);

        EntityModel<Pedido> recurso = EntityModel.of(pedido,
                linkTo(methodOn(this.getClass()).obtenerPedido(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("pedidos"));

        return ResponseEntity.ok(recurso);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<CollectionModel<EntityModel<Pedido>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<EntityModel<Pedido>> recursos = pedidoService.buscarPorCliente(clienteId).stream()
                .map(pedido -> EntityModel.of(pedido,
                        linkTo(methodOn(this.getClass()).obtenerPedido(pedido.getId())).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Pedido>> coleccion = CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).obtenerPorCliente(clienteId)).withSelfRel());

        return ResponseEntity.ok(coleccion);
    }

    @PostMapping
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
    public ResponseEntity<EntityModel<Pedido>> actualizarEstado(@PathVariable Long id,
                                                                 @RequestParam String estado) {
        Pedido pedido = pedidoService.actualizarEstado(id, estado);

        EntityModel<Pedido> recurso = EntityModel.of(pedido,
                linkTo(methodOn(this.getClass()).obtenerPedido(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("pedidos"));

        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
