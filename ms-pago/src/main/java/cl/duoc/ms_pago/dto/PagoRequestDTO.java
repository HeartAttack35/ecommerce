package cl.duoc.ms_pago.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoRequestDTO {

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotBlank(message = "El método de pago no puede estar vacío")
    @Pattern(regexp = "TARJETA|TRANSFERENCIA|EFECTIVO", message = "El método debe ser TARJETA, TRANSFERENCIA o EFECTIVO")
    private String metodoPago;
}
