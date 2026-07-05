package cl.duoc.ms_pago.service;

import cl.duoc.ms_pago.dto.PagoRequestDTO;
import cl.duoc.ms_pago.model.Pago;
import cl.duoc.ms_pago.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoService pagoService;

    // ─── Happy Path ───────────────────────────────────────────────────────────

    @Test
    void deberiaCrearPago() {
        // Arrange
        PagoRequestDTO dto = PagoRequestDTO.builder()
                .pedidoId(10L).monto(new BigDecimal("150.00")).metodoPago("TARJETA").build();

        Pago guardado = Pago.builder().id(1L).pedidoId(10L)
                .monto(new BigDecimal("150.00")).metodoPago("TARJETA")
                .estado("PENDIENTE").fechaPago(LocalDateTime.now()).build();
        when(pagoRepository.save(any(Pago.class))).thenReturn(guardado);

        // Act
        Pago resultado = pagoService.crear(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("TARJETA", resultado.getMetodoPago());
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void deberiaListarTodosLosPagos() {
        // Arrange
        when(pagoRepository.findAll()).thenReturn(
                List.of(Pago.builder().id(1L).build(), Pago.builder().id(2L).build()));

        // Act
        List<Pago> resultado = pagoService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaActualizarEstadoDePago() {
        // Arrange
        Pago pago = Pago.builder().id(1L).estado("PENDIENTE").build();
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        // Act
        Pago resultado = pagoService.actualizarEstado(1L, "APROBADO");

        // Assert
        assertEquals("APROBADO", resultado.getEstado());
    }

    @Test
    void deberiaEliminarPago() {
        // Arrange
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(Pago.builder().id(1L).build()));

        // Act
        pagoService.eliminar(1L);

        // Assert
        verify(pagoRepository).deleteById(1L);
    }

    // ─── Error Path ──────────────────────────────────────────────────────────

    @Test
    void deberiaLanzarExcepcionCuandoPagoNoExiste() {
        // Arrange
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> pagoService.buscarPorId(99L));
    }
}
