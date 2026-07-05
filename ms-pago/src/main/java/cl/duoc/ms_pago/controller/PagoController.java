package cl.duoc.ms_pago.controller;

import cl.duoc.ms_pago.dto.PagoRequestDTO;
import cl.duoc.ms_pago.model.Pago;
import cl.duoc.ms_pago.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Pago>>> listarTodos() {
        List<EntityModel<Pago>> recursos = pagoService.listarTodos().stream()
                .map(pago -> EntityModel.of(pago,
                        linkTo(methodOn(this.getClass()).obtenerPago(pago.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodos()).withRel("pagos")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Pago>> coleccion = CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodos()).withSelfRel());

        return ResponseEntity.ok(coleccion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Pago>> obtenerPago(@PathVariable Long id) {
        Pago pago = pagoService.buscarPorId(id);

        EntityModel<Pago> recurso = EntityModel.of(pago,
                linkTo(methodOn(this.getClass()).obtenerPago(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("pagos"));

        return ResponseEntity.ok(recurso);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EntityModel<Pago>> obtenerPorPedido(@PathVariable Long pedidoId) {
        Pago pago = pagoService.buscarPorPedido(pedidoId);

        EntityModel<Pago> recurso = EntityModel.of(pago,
                linkTo(methodOn(this.getClass()).obtenerPago(pago.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("pagos"));

        return ResponseEntity.ok(recurso);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Pago>> crear(@Valid @RequestBody PagoRequestDTO dto) {
        Pago pago = pagoService.crear(dto);

        EntityModel<Pago> recurso = EntityModel.of(pago,
                linkTo(methodOn(this.getClass()).obtenerPago(pago.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("pagos"));

        return ResponseEntity.created(
                linkTo(methodOn(this.getClass()).obtenerPago(pago.getId())).toUri()
        ).body(recurso);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EntityModel<Pago>> actualizarEstado(@PathVariable Long id,
                                                               @RequestParam String estado) {
        Pago pago = pagoService.actualizarEstado(id, estado);

        EntityModel<Pago> recurso = EntityModel.of(pago,
                linkTo(methodOn(this.getClass()).obtenerPago(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("pagos"));

        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
