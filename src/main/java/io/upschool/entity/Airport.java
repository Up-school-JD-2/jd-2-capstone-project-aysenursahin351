package io.upschool.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "airport")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int status = 1; // Default olarak 1

    @OneToMany(mappedBy = "departureAirport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Route> departureRoutes = new ArrayList<>();

    @OneToMany(mappedBy = "arrivalAirport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Route> arrivalRoutes = new ArrayList<>();

    @Override
    public String toString() {
        return "Airport{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", code='" + code + '\'' +
                ", status=" + status +
                '}';
    }
}

