package com.cabinet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idpatient;

    @NotBlank(message = "Le nom est obligatoire")
    private String nompatient;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenompatient;

    private String adrpatient;

    @Email(message = "Email invalide")
    private String emailpatient;

    // Compte de connexion du patient (jamais exposé en JSON)
    @OneToOne
    @JoinColumn(name = "id_utilisateur")
    @JsonIgnore
    private Utilisateur utilisateur;

    // Un patient a plusieurs rendez-vous
    @OneToMany(mappedBy = "patient")
    @JsonIgnore
    private List<Rendezvous> rendezvous = new ArrayList<>();
}
