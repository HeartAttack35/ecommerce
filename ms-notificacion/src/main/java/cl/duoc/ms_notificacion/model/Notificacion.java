package cl.duoc.ms_notificacion.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private String tipo; // EMAIL, SMS, PUSH

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false, length = 2000)
    private String mensaje;

    @Column(nullable = false)
    private String estado; // PENDIENTE, ENVIADO, FALLIDO

    @Column(nullable = false)
    private LocalDateTime fechaEnvio;
}
