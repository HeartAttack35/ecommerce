package cl.duoc.ms_pedido.service;

import cl.duoc.ms_pedido.client.InventarioClient;
import cl.duoc.ms_pedido.dto.PedidoRequestDTO;
import cl.duoc.ms_pedido.model.Pedido;
import cl.duoc.ms_pedido.repository.PedidoRepository;
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
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private PedidoService pedidoService;

    // ─── Happy Path ───────────────────────────────────────────────────────────

    @Test
    void deberiaCrearPedidoCuandoHayStock() {
        // Arrange
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L).productoId(100L).cantidad(2).build();

        when(inventarioClient.obtenerStockPorProducto(100L)).thenReturn(10);

        Pedido pedidoGuardado = Pedido.builder()
                .id(1L).clienteId(1L).productoId(100L).cantidad(2)
                .total(BigDecimal.ZERO).estado("PENDIENTE")
                .fechaPedido(LocalDateTime.now()).build();
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        // Act
        Pedido resultado = pedidoService.crear(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void deberiaListarTodosLosPedidos() {
        // Arrange
        List<Pedido> pedidos = List.of(
                Pedido.builder().id(1L).estado("PENDIENTE").build(),
                Pedido.builder().id(2L).estado("CONFIRMADO").build()
        );
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        // Act
        List<Pedido> resultado = pedidoService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        verify(pedidoRepository).findAll();
    }

    @Test
    void deberiaBuscarPedidoPorId() {
        // Arrange
        Pedido pedido = Pedido.builder().id(5L).estado("PENDIENTE").build();
        when(pedidoRepository.findById(5L)).thenReturn(Optional.of(pedido));

        // Act
        Pedido resultado = pedidoService.buscarPorId(5L);

        // Assert
        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
    }

    @Test
    void deberiaActualizarEstadoDePedido() {
        // Arrange
        Pedido pedido = Pedido.builder().id(1L).estado("PENDIENTE").build();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act
        Pedido resultado = pedidoService.actualizarEstado(1L, "CONFIRMADO");

        // Assert
        assertEquals("CONFIRMADO", resultado.getEstado());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void deberiaEliminarPedido() {
        // Arrange
        Pedido pedido = Pedido.builder().id(1L).build();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act
        pedidoService.eliminar(1L);

        // Assert
        verify(pedidoRepository).deleteById(1L);
    }

    // ─── Error Paths ─────────────────────────────────────────────────────────

    @Test
    void deberiaLanzarExcepcionCuandoNoHayStock() {
        // Arrange
        PedidoRequestDTO dto = PedidoRequestDTO.builder()
                .clienteId(1L).productoId(100L).cantidad(20).build();
        when(inventarioClient.obtenerStockPorProducto(100L)).thenReturn(5);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pedidoService.crear(dto));

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoPedidoNoExiste() {
        // Arrange
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> pedidoService.buscarPorId(99L));
    }
}
