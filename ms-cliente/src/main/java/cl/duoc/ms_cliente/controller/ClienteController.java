package cl.duoc.ms_cliente.controller;

import cl.duoc.ms_cliente.dto.ClienteRequestDTO;
import cl.duoc.ms_cliente.model.Cliente;
import cl.duoc.ms_cliente.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Cliente>>> listarTodos() {
        List<EntityModel<Cliente>> recursos = clienteService.listarTodos().stream()
                .map(cliente -> EntityModel.of(cliente,
                        linkTo(methodOn(this.getClass()).obtenerCliente(cliente.getId())).withSelfRel(),
                        linkTo(methodOn(this.getClass()).listarTodos()).withRel("clientes")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Cliente>> coleccion = CollectionModel.of(recursos,
                linkTo(methodOn(this.getClass()).listarTodos()).withSelfRel());

        return ResponseEntity.ok(coleccion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> obtenerCliente(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);

        EntityModel<Cliente> recurso = EntityModel.of(cliente,
                linkTo(methodOn(this.getClass()).obtenerCliente(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("clientes"));

        return ResponseEntity.ok(recurso);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Cliente>> crear(@Valid @RequestBody ClienteRequestDTO dto) {
        Cliente cliente = clienteService.crear(dto);

        EntityModel<Cliente> recurso = EntityModel.of(cliente,
                linkTo(methodOn(this.getClass()).obtenerCliente(cliente.getId())).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("clientes"));

        return ResponseEntity.created(
                linkTo(methodOn(this.getClass()).obtenerCliente(cliente.getId())).toUri()
        ).body(recurso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> actualizar(@PathVariable Long id,
                                                            @Valid @RequestBody ClienteRequestDTO dto) {
        Cliente cliente = clienteService.actualizar(id, dto);

        EntityModel<Cliente> recurso = EntityModel.of(cliente,
                linkTo(methodOn(this.getClass()).obtenerCliente(id)).withSelfRel(),
                linkTo(methodOn(this.getClass()).listarTodos()).withRel("clientes"));

        return ResponseEntity.ok(recurso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
