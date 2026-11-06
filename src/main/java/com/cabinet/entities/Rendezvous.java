package com.cabinet.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Classe d'association entre Medecin et Patient.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rendezvous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrdv;

    @NotNull(message = "La date est obligatoire")
    private LocalDate daterdv;

    @NotNull(message = "L'heure est obligatoire")
    private LocalTime heurerdv;

    // PROGRAMME -> PASSE ou ANNULE
    @Enumerated(EnumType.STRING)
    private StatutRdv statut = StatutRdv.PROGRAMME;

    @NotNull(message = "Le médecin est obligatoire")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_medecin")
    private Medecin medecin;

    @NotNull(message = "Le patient est obligatoire")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_patient")
    private Patient patient;

    // 1..1 avec Consultation
    @OneToOne(mappedBy = "rendezvous", cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties("rendezvous")
    private Consultation consultation;
}
