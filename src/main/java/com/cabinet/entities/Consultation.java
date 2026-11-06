package com.cabinet.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcons;

    @NotNull(message = "La date de consultation est obligatoire")
    private LocalDate datecons;

    @Column(length = 2000)
    private String recapcons;

    @Column(length = 4000)
    private String ordonnance;

    @Column(length = 4000)
    private String certificat;

    // Côté propriétaire de la relation 1..1
    @NotNull(message = "Le rendez-vous est obligatoire")
    @OneToOne(optional = false)
    @JoinColumn(name = "idrdv", unique = true)
    @JsonIgnoreProperties("consultation")
    private Rendezvous rendezvous;
}
