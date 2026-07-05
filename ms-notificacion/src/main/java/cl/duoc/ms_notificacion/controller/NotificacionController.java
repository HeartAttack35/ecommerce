package cl.duoc.ms_notificacion.controller;

import cl.duoc.ms_notificacion.dto.NotificacionRequestDTO;
import cl.duoc.ms_notificacion.model.Notificacion;
import cl.duoc.ms_notificacion.service.NotificacionService;
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
@RequestMapping("/notificaciones")
@Tag(name = "Notificaciones", description = "Operaciones para el envío y consulta de notificaciones a clientes")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las notificaciones", description = "Retorna el historial completo de notificaciones enviadas")
    public ResponseEntity<CollectionModel<EntityModel<Notificacion>>> listarTodas() {
        List<EntityModel<Notificacion>> recursos = notificacionService.listarTodas().stream()
                .map(noti -> EntityModel.of(noti,
                        linkTo(methodOn(this.getClass()).obtenerNotificacion(noti.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodas()).withRel("notificaciones")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodas()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener notificación por ID", description = "Retorna los datos de una notificación específica según su identificador")
    public ResponseEntity<EntityModel<Notificacion>> obtenerNotificacion(@PathVariable Long id) {
        Notificacion notificacion = notificacionService.buscarPorId(id);
        EntityModel<Notificacion> recurso = EntityModel.of(notificacion,
                linkTo(methodOn(this.getClass()).obtenerNotificacion(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodas()).withRel("notificaciones"));
        return ResponseEntity.ok(recurso);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener notificaciones por cliente", description = "Retorna el historial de notificaciones enviadas a un cliente específico")
    public ResponseEntity<CollectionModel<EntityModel<Notificacion>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<EntityModel<Notificacion>> recursos = notificacionService.buscarPorCliente(clienteId).stream()
                .map(noti -> EntityModel.of(noti,
                        linkTo(methodOn(this.getClass()).obtenerNotificacion(noti.getId())).withSelfRel()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).obtenerPorCliente(clienteId)).withSelfRel()));
    }

    @PostMapping
    @Operation(summary = "Enviar notificación", description = "Registra y envía una notificación (EMAIL, SMS, PUSH) a un cliente")
    public ResponseEntity<EntityModel<Notificacion>> enviar(@Valid @RequestBody NotificacionRequestDTO dto) {
        Notificacion notificacion = notificacionService.enviar(dto);
        EntityModel<Notificacion> recurso = EntityModel.of(notificacion,
                linkTo(methodOn(this.getClass()).obtenerNotificacion(notificacion.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodas()).withRel("notificaciones"));
        return ResponseEntity.created(
                linkTo(methodOn(this.getClass()).obtenerNotificacion(notificacion.getId())).toUri()
        ).body(recurso);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar notificación", description = "Elimina un registro de notificación del historial")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
