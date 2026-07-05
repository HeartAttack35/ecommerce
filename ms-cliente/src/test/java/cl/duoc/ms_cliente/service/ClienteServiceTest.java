package cl.duoc.ms_cliente.service;

import cl.duoc.ms_cliente.dto.ClienteRequestDTO;
import cl.duoc.ms_cliente.model.Cliente;
import cl.duoc.ms_cliente.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    // ─── Happy Path ───────────────────────────────────────────────────────────

    @Test
    void deberiaCrearClienteCuandoEmailNoExiste() {
        // Arrange
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nombre("Juan").apellido("Pérez").email("juan@test.com")
                .telefono("999").direccion("Calle 1").build();

        when(clienteRepository.existsByEmail("juan@test.com")).thenReturn(false);

        Cliente guardado = Cliente.builder().id(1L).nombre("Juan").apellido("Pérez")
                .email("juan@test.com").telefono("999").direccion("Calle 1").build();
        when(clienteRepository.save(any(Cliente.class))).thenReturn(guardado);

        // Act
        Cliente resultado = clienteService.crear(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("juan@test.com", resultado.getEmail());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void deberiaListarTodosLosClientes() {
        // Arrange
        when(clienteRepository.findAll()).thenReturn(
                List.of(Cliente.builder().id(1L).build(), Cliente.builder().id(2L).build()));

        // Act
        List<Cliente> resultado = clienteService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        verify(clienteRepository).findAll();
    }

    @Test
    void deberiaBuscarClientePorId() {
        // Arrange
        Cliente cliente = Cliente.builder().id(3L).email("test@test.com").build();
        when(clienteRepository.findById(3L)).thenReturn(Optional.of(cliente));

        // Act
        Cliente resultado = clienteService.buscarPorId(3L);

        // Assert
        assertEquals(3L, resultado.getId());
    }

    @Test
    void deberiaActualizarCliente() {
        // Arrange
        Cliente cliente = Cliente.builder().id(1L).nombre("Viejo").email("old@test.com").build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nombre("Nuevo").apellido("A").email("new@test.com")
                .telefono("111").direccion("Dir").build();

        // Act
        Cliente resultado = clienteService.actualizar(1L, dto);

        // Assert
        assertEquals("Nuevo", resultado.getNombre());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void deberiaEliminarCliente() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(Cliente.builder().id(1L).build()));

        // Act
        clienteService.eliminar(1L);

        // Assert
        verify(clienteRepository).deleteById(1L);
    }

    // ─── Error Paths ─────────────────────────────────────────────────────────

    @Test
    void deberiaLanzarExcepcionCuandoEmailDuplicado() {
        // Arrange
        ClienteRequestDTO dto = ClienteRequestDTO.builder()
                .nombre("Ana").apellido("G").email("dup@test.com")
                .telefono("222").direccion("Dir 2").build();
        when(clienteRepository.existsByEmail("dup@test.com")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> clienteService.crear(dto));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoClienteNoExiste() {
        // Arrange
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> clienteService.buscarPorId(99L));
    }
}
