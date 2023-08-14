package io.upschool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    private Long routeId;
    private Date departureDate;
    private double price;
    @Column(nullable = false)
    private String name;
//    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
//    private Long companyId;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 1") // Default olarak 1
    private int status;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int availableSeats; // Müsait koltuk sayısı
    @Column(nullable = false)
    private int ticketsSold; // Track the number of tickets sold
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "flight_route_id", referencedColumnName = "id")
    //private Long flight_route_id;
    private Route route;


}

