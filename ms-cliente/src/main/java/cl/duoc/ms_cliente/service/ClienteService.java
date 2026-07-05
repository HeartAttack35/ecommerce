package cl.duoc.ms_cliente.service;

import cl.duoc.ms_cliente.dto.ClienteRequestDTO;
import cl.duoc.ms_cliente.model.Cliente;
import cl.duoc.ms_cliente.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado con id: " + id));
    }

    public Cliente crear(ClienteRequestDTO dto) {
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un cliente con el email: " + dto.getEmail());
        }
        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .build();
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = buscarPorId(id);
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        clienteRepository.deleteById(id);
    }
}
