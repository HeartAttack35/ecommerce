package cl.duoc.ms_envio.service;

import cl.duoc.ms_envio.dto.EnvioRequestDTO;
import cl.duoc.ms_envio.model.Envio;
import cl.duoc.ms_envio.repository.EnvioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private EnvioService envioService;

    // ─── Happy Path ───────────────────────────────────────────────────────────

    @Test
    void deberiaCrearEnvio() {
        // Arrange
        EnvioRequestDTO dto = EnvioRequestDTO.builder()
                .pedidoId(1L).direccionDestino("Calle 1").transportista("DHL")
                .fechaEstimadaEntrega(LocalDateTime.now().plusDays(3)).build();

        Envio guardado = Envio.builder().id(1L).pedidoId(1L).transportista("DHL")
                .estado("PREPARANDO").numeroSeguimiento("ABC12345")
                .fechaEstimadaEntrega(LocalDateTime.now().plusDays(3)).build();
        when(envioRepository.save(any(Envio.class))).thenReturn(guardado);

        // Act
        Envio resultado = envioService.crear(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("PREPARANDO", resultado.getEstado());
        assertNotNull(resultado.getNumeroSeguimiento());
        verify(envioRepository).save(any(Envio.class));
    }

    @Test
    void deberiaListarTodosLosEnvios() {
        // Arrange
        when(envioRepository.findAll()).thenReturn(
                List.of(Envio.builder().id(1L).build(), Envio.builder().id(2L).build()));

        // Act
        List<Envio> resultado = envioService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaActualizarEstadoDeEnvio() {
        // Arrange
        Envio envio = Envio.builder().id(1L).estado("PREPARANDO").build();
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);

        // Act
        Envio resultado = envioService.actualizarEstado(1L, "EN_CAMINO");

        // Assert
        assertEquals("EN_CAMINO", resultado.getEstado());
    }

    @Test
    void deberiaEliminarEnvio() {
        // Arrange
        when(envioRepository.findById(1L)).thenReturn(Optional.of(Envio.builder().id(1L).build()));

        // Act
        envioService.eliminar(1L);

        // Assert
        verify(envioRepository).deleteById(1L);
    }

    // ─── Error Path ──────────────────────────────────────────────────────────

    @Test
    void deberiaLanzarExcepcionCuandoEnvioNoExiste() {
        // Arrange
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> envioService.buscarPorId(99L));
    }
}
