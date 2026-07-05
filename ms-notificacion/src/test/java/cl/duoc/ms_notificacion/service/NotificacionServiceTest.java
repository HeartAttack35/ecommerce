package cl.duoc.ms_notificacion.service;

import cl.duoc.ms_notificacion.dto.NotificacionRequestDTO;
import cl.duoc.ms_notificacion.model.Notificacion;
import cl.duoc.ms_notificacion.repository.NotificacionRepository;
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
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    // ─── Happy Path ───────────────────────────────────────────────────────────

    @Test
    void deberiaEnviarNotificacion() {
        // Arrange
        NotificacionRequestDTO dto = NotificacionRequestDTO.builder()
                .clienteId(1L).tipo("EMAIL").asunto("Bienvenido").mensaje("Hola!").build();

        Notificacion guardada = Notificacion.builder().id(1L).clienteId(1L)
                .tipo("EMAIL").asunto("Bienvenido").mensaje("Hola!")
                .estado("ENVIADO").fechaEnvio(LocalDateTime.now()).build();
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(guardada);

        // Act
        Notificacion resultado = notificacionService.enviar(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("ENVIADO", resultado.getEstado());
        assertEquals("EMAIL", resultado.getTipo());
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void deberiaListarTodasLasNotificaciones() {
        // Arrange
        when(notificacionRepository.findAll()).thenReturn(
                List.of(Notificacion.builder().id(1L).build(),
                        Notificacion.builder().id(2L).build()));

        // Act
        List<Notificacion> resultado = notificacionService.listarTodas();

        // Assert
        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaBuscarNotificacionesPorCliente() {
        // Arrange
        when(notificacionRepository.findByClienteId(1L)).thenReturn(
                List.of(Notificacion.builder().id(1L).clienteId(1L).build()));

        // Act
        List<Notificacion> resultado = notificacionService.buscarPorCliente(1L);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getClienteId());
    }

    @Test
    void deberiaEliminarNotificacion() {
        // Arrange
        when(notificacionRepository.findById(1L))
                .thenReturn(Optional.of(Notificacion.builder().id(1L).build()));

        // Act
        notificacionService.eliminar(1L);

        // Assert
        verify(notificacionRepository).deleteById(1L);
    }

    // ─── Error Path ──────────────────────────────────────────────────────────

    @Test
    void deberiaLanzarExcepcionCuandoNotificacionNoExiste() {
        // Arrange
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
                () -> notificacionService.buscarPorId(99L));
    }
}
