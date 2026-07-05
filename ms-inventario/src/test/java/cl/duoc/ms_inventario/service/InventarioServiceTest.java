package cl.duoc.ms_inventario.service;

import cl.duoc.ms_inventario.dto.InventarioRequestDTO;
import cl.duoc.ms_inventario.model.Inventario;
import cl.duoc.ms_inventario.repository.InventarioRepository;
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
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    // ─── Happy Path ───────────────────────────────────────────────────────────

    @Test
    void deberiaCrearInventario() {
        // Arrange
        InventarioRequestDTO dto = InventarioRequestDTO.builder()
                .productoId(1L).cantidadDisponible(100).cantidadReservada(0).ubicacion("A1").build();

        when(inventarioRepository.existsByProductoId(1L)).thenReturn(false);

        Inventario guardado = Inventario.builder().id(1L).productoId(1L)
                .cantidadDisponible(100).cantidadReservada(0).ubicacion("A1").build();
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(guardado);

        // Act
        Inventario resultado = inventarioService.crear(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(100, resultado.getCantidadDisponible());
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void deberiaListarTodo() {
        // Arrange
        when(inventarioRepository.findAll()).thenReturn(
                List.of(Inventario.builder().id(1L).build()));

        // Act
        List<Inventario> resultado = inventarioService.listarTodos();

        // Assert
        assertEquals(1, resultado.size());
    }

    @Test
    void deberiaBuscarPorProducto() {
        // Arrange
        Inventario inv = Inventario.builder().id(1L).productoId(5L).cantidadDisponible(20).build();
        when(inventarioRepository.findByProductoId(5L)).thenReturn(Optional.of(inv));

        // Act
        Inventario resultado = inventarioService.buscarPorProducto(5L);

        // Assert
        assertEquals(20, resultado.getCantidadDisponible());
    }

    @Test
    void deberiaActualizarInventario() {
        // Arrange
        Inventario inv = Inventario.builder().id(1L).cantidadDisponible(10).build();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inv));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inv);

        InventarioRequestDTO dto = InventarioRequestDTO.builder()
                .productoId(1L).cantidadDisponible(50).cantidadReservada(5).ubicacion("B2").build();

        // Act
        Inventario resultado = inventarioService.actualizar(1L, dto);

        // Assert
        assertEquals(50, resultado.getCantidadDisponible());
    }

    @Test
    void deberiaEliminarInventario() {
        // Arrange
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(Inventario.builder().id(1L).build()));

        // Act
        inventarioService.eliminar(1L);

        // Assert
        verify(inventarioRepository).deleteById(1L);
    }

    // ─── Error Paths ─────────────────────────────────────────────────────────

    @Test
    void deberiaLanzarExcepcionSiProductoYaTieneInventario() {
        // Arrange
        InventarioRequestDTO dto = InventarioRequestDTO.builder()
                .productoId(1L).cantidadDisponible(10).cantidadReservada(0).ubicacion("A1").build();
        when(inventarioRepository.existsByProductoId(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> inventarioService.crear(dto));
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void deberiaLanzarExcepcionSiInventarioNoExiste() {
        // Arrange
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> inventarioService.buscarPorId(99L));
    }
}
