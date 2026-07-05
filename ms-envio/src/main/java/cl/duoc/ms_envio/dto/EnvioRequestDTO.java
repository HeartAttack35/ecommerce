package cl.duoc.ms_envio.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioRequestDTO {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotBlank(message = "La dirección de destino no puede estar vacía")
    private String direccionDestino;

    @NotBlank(message = "El transportista no puede estar vacío")
    private String transportista;

    @NotNull(message = "La fecha estimada de entrega es obligatoria")
    private LocalDateTime fechaEstimadaEntrega;
}
