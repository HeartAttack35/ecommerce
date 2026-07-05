package cl.duoc.ms_envio.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId;

    @Column(nullable = false)
    private String direccionDestino;

    @Column(nullable = false)
    private String estado; // PREPARANDO, EN_CAMINO, ENTREGADO

    @Column(nullable = false)
    private String transportista;

    @Column
    private String numeroSeguimiento;

    @Column(nullable = false)
    private LocalDateTime fechaEstimadaEntrega;
}
