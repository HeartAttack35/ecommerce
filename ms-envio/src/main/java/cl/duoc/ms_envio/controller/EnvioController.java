package cl.duoc.ms_envio.controller;

import cl.duoc.ms_envio.dto.EnvioRequestDTO;
import cl.duoc.ms_envio.model.Envio;
import cl.duoc.ms_envio.service.EnvioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/envios")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public ResponseEntity<List<Envio>> listarTodos() {
        return ResponseEntity.ok(envioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerEnvio(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.buscarPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<Envio> obtenerPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(envioService.buscarPorPedido(pedidoId));
    }

    @PostMapping
    public ResponseEntity<Envio> crear(@Valid @RequestBody EnvioRequestDTO dto) {
        Envio envio = envioService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(envio.getId()).toUri();
        return ResponseEntity.created(location).body(envio);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Envio> actualizarEstado(@PathVariable Long id,
                                                  @RequestParam String estado) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        envioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
