package cl.duoc.ms_resena.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resenas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del cliente que escribe la reseña */
    @Column(nullable = false)
    private Long clienteId;

    /** ID del producto reseñado */
    @Column(nullable = false)
    private Long productoId;

    /** Puntuación de 1 a 5 estrellas */
    @Column(nullable = false)
    private Integer puntuacion;

    /** Texto de la reseña */
    @Column(nullable = false, length = 1000)
    private String comentario;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime creadaEn = LocalDateTime.now();

    /** true mientras la reseña no ha sido moderada/eliminada */
    @Column(nullable = false)
    @Builder.Default
    private Boolean visible = true;
}
