package cl.duoc.ms_producto.service;

import cl.duoc.ms_producto.dto.ProductoRequestDTO;
import cl.duoc.ms_producto.model.Producto;
import cl.duoc.ms_producto.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    // ─── Happy Path ───────────────────────────────────────────────────────────

    @Test
    void deberiaCrearProducto() {
        // Arrange
        ProductoRequestDTO dto = ProductoRequestDTO.builder()
                .nombre("Laptop").descripcion("Portátil").precio(new BigDecimal("999.99"))
                .stock(50).categoria("Electrónica").build();

        Producto guardado = Producto.builder().id(1L).nombre("Laptop")
                .precio(new BigDecimal("999.99")).stock(50).categoria("Electrónica").build();
        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        // Act
        Producto resultado = productoService.crear(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laptop", resultado.getNombre());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void deberiaListarTodosLosProductos() {
        // Arrange
        when(productoRepository.findAll()).thenReturn(
                List.of(Producto.builder().id(1L).build(), Producto.builder().id(2L).build()));

        // Act
        List<Producto> resultado = productoService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaBuscarProductoPorId() {
        // Arrange
        Producto producto = Producto.builder().id(10L).nombre("Mouse").build();
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        // Act
        Producto resultado = productoService.buscarPorId(10L);

        // Assert
        assertEquals("Mouse", resultado.getNombre());
    }

    @Test
    void deberiaActualizarProducto() {
        // Arrange
        Producto producto = Producto.builder().id(1L).nombre("Viejo").stock(10).build();
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoRequestDTO dto = ProductoRequestDTO.builder()
                .nombre("Nuevo").precio(new BigDecimal("50")).stock(20).categoria("Tech").build();

        // Act
        Producto resultado = productoService.actualizar(1L, dto);

        // Assert
        assertEquals("Nuevo", resultado.getNombre());
        verify(productoRepository).save(producto);
    }

    @Test
    void deberiaEliminarProducto() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(Producto.builder().id(1L).build()));

        // Act
        productoService.eliminar(1L);

        // Assert
        verify(productoRepository).deleteById(1L);
    }

    // ─── Error Path ──────────────────────────────────────────────────────────

    @Test
    void deberiaLanzarExcepcionCuandoProductoNoExiste() {
        // Arrange
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> productoService.buscarPorId(99L));
    }
}
