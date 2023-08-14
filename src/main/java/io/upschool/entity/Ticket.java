package io.upschool.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.context.annotation.Lazy;

@Entity
@Table(name = "ticket")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Lazy

public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;
    @Column(nullable = false)
    private String passengerName;
    @Column(nullable = false)
    private String seatNumber;
    @Column(nullable = false)
    private boolean isConfirmed; // Bilet onaylandı mı?
    @Column(nullable = false)
    private String creditCardNumber;
    @Column(nullable = false, unique = true)
    private String pnr;
    @Column(nullable = false)
    private int cancellation = 0; // Default olarak false
    @Builder.Default
    @Column(nullable = false)
    private int status = 1; // Default olarak 1

}
