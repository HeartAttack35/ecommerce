package cl.duoc.ms_envio.controller;

import cl.duoc.ms_envio.dto.EnvioRequestDTO;
import cl.duoc.ms_envio.model.Envio;
import cl.duoc.ms_envio.service.EnvioService;
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
@RequestMapping("/envios")
@Tag(name = "Envíos", description = "Operaciones para el despacho y seguimiento de envíos")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los envíos", description = "Retorna la lista completa de envíos registrados en el sistema")
    public ResponseEntity<CollectionModel<EntityModel<Envio>>> listarTodos() {
        List<EntityModel<Envio>> recursos = envioService.listarTodos().stream()
                .map(envio -> EntityModel.of(envio,
                        linkTo(methodOn(this.getClass()).obtenerEnvio(envio.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodos()).withRel("envios")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener envío por ID", description = "Retorna los datos de un envío específico según su identificador")
    public ResponseEntity<EntityModel<Envio>> obtenerEnvio(@PathVariable Long id) {
        Envio envio = envioService.buscarPorId(id);
        EntityModel<Envio> recurso = EntityModel.of(envio,
                linkTo(methodOn(this.getClass()).obtenerEnvio(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("envios"));
        return ResponseEntity.ok(recurso);
    }

    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Obtener envío por pedido", description = "Retorna el envío asociado a un pedido específico")
    public ResponseEntity<EntityModel<Envio>> obtenerPorPedido(@PathVariable Long pedidoId) {
        Envio envio = envioService.buscarPorPedido(pedidoId);
        EntityModel<Envio> recurso = EntityModel.of(envio,
                linkTo(methodOn(this.getClass()).obtenerEnvio(envio.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("envios"));
        return ResponseEntity.ok(recurso);
    }

    @PostMapping
    @Operation(summary = "Crear envío", description = "Registra un nuevo envío en estado PREPARANDO y genera número de seguimiento automático")
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
    @Operation(summary = "Actualizar estado del envío", description = "Cambia el estado del envío (PREPARANDO, EN_CAMINO, ENTREGADO)")
    public ResponseEntity<EntityModel<Envio>> actualizarEstado(@PathVariable Long id,
                                                                @RequestParam String estado) {
        Envio envio = envioService.actualizarEstado(id, estado);
        EntityModel<Envio> recurso = EntityModel.of(envio,
                linkTo(methodOn(this.getClass()).obtenerEnvio(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("envios"));
        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar envío", description = "Elimina permanentemente un envío del sistema")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        envioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
