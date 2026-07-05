package cl.duoc.ms_notificacion.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionRequestDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotBlank(message = "El tipo de notificación no puede estar vacío")
    @Pattern(regexp = "EMAIL|SMS|PUSH", message = "El tipo debe ser EMAIL, SMS o PUSH")
    private String tipo;

    @NotBlank(message = "El asunto no puede estar vacío")
    private String asunto;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 2000, message = "El mensaje no puede superar los 2000 caracteres")
    private String mensaje;
}
