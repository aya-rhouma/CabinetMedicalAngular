package com.cabinet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class Medecin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    private String adr;

    @Email(message = "Email invalide")
    private String email;

    // Un médecin a une seule spécialité (1..1), une spécialité a plusieurs médecins (1..*)
    @NotNull(message = "La spécialité est obligatoire")
    @ManyToOne(optional = false)
    @JoinColumn(name = "idspec")
    private Specialite specialite;

    // Compte de connexion du médecin (jamais exposé en JSON)
    @OneToOne
    @JoinColumn(name = "id_utilisateur")
    @JsonIgnore
    private Utilisateur utilisateur;

    // Un médecin a plusieurs rendez-vous
    @OneToMany(mappedBy = "medecin")
    @JsonIgnore
    private List<Rendezvous> rendezvous = new ArrayList<>();
}
