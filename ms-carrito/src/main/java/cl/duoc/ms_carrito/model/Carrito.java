package cl.duoc.ms_carrito.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del cliente dueño del carrito */
    @Column(nullable = false)
    private Long clienteId;

    /** ACTIVO | CONFIRMADO | ABANDONADO */
    @Column(nullable = false)
    @Builder.Default
    private String estado = "ACTIVO";

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "carritoId")
    @Builder.Default
    private List<ItemCarrito> items = new ArrayList<>();
}
