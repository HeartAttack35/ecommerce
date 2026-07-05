package cl.duoc.ms_pago.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String metodoPago; // TARJETA, TRANSFERENCIA, EFECTIVO

    @Column(nullable = false)
    private String estado; // PENDIENTE, APROBADO, RECHAZADO

    @Column(nullable = false)
    private LocalDateTime fechaPago;
}
