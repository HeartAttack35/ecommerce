package cl.duoc.ms_envio.controller;

import cl.duoc.ms_envio.dto.EnvioRequestDTO;
import cl.duoc.ms_envio.model.Envio;
import cl.duoc.ms_envio.service.EnvioService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/envios")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Envio>>> listarTodos() {
        List<EntityModel<Envio>> recursos = envioService.listarTodos().stream()
                .map(envio -> EntityModel.of(envio,
                        linkTo(methodOn(this.getClass()).obtenerEnvio(envio.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodos()).withRel("envios")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Envio>> coleccion = CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodos()).withSelfRel());

        return ResponseEntity.ok(coleccion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Envio>> obtenerEnvio(@PathVariable Long id) {
        Envio envio = envioService.buscarPorId(id);

        EntityModel<Envio> recurso = EntityModel.of(envio,
                linkTo(methodOn(this.getClass()).obtenerEnvio(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("envios"));

        return ResponseEntity.ok(recurso);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EntityModel<Envio>> obtenerPorPedido(@PathVariable Long pedidoId) {
        Envio envio = envioService.buscarPorPedido(pedidoId);

        EntityModel<Envio> recurso = EntityModel.of(envio,
                linkTo(methodOn(this.getClass()).obtenerEnvio(envio.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("envios"));

        return ResponseEntity.ok(recurso);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Envio>> crear(@Valid @RequestBody EnvioRequestDTO dto) {
        Envio envio = envioService.crear(dto);

        EntityModel<Envio> recurso = EntityModel.of(envio,
                linkTo(methodOn(this.getClass()).obtenerEnvio(envio.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("envios"));

        return ResponseEntity.created(
                linkTo(methodOn(this.getClass()).obtenerEnvio(envio.getId())).toUri()
        ).body(recurso);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EntityModel<Envio>> actualizarEstado(@PathVariable Long id,
                                                                @RequestParam String estado) {
        Envio envio = envioService.actualizarEstado(id, estado);

        EntityModel<Envio> recurso = EntityModel.of(envio,
                linkTo(methodOn(this.getClass()).obtenerEnvio(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("envios"));

        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        envioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
