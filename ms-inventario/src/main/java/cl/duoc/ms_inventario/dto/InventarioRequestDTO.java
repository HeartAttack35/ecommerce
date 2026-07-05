package cl.duoc.ms_inventario.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioRequestDTO {

    @NotNull(message = "El ID de producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad disponible es obligatoria")
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    private Integer cantidadDisponible;

    @NotNull(message = "La cantidad reservada es obligatoria")
    @Min(value = 0, message = "La cantidad reservada no puede ser negativa")
    private Integer cantidadReservada;

    @NotBlank(message = "La ubicación no puede estar vacía")
    private String ubicacion;
}
