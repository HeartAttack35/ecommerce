package cl.duoc.ms_notificacion.controller;

import cl.duoc.ms_notificacion.dto.NotificacionRequestDTO;
import cl.duoc.ms_notificacion.model.Notificacion;
import cl.duoc.ms_notificacion.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> listarTodas() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtenerNotificacion(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Notificacion>> obtenerPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(notificacionService.buscarPorCliente(clienteId));
    }

    @PostMapping
    public ResponseEntity<Notificacion> enviar(@Valid @RequestBody NotificacionRequestDTO dto) {
        Notificacion notificacion = notificacionService.enviar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(notificacion.getId()).toUri();
        return ResponseEntity.created(location).body(notificacion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
