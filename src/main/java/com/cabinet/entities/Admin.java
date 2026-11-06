package com.cabinet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Administrateur du cabinet. Son login et son mot de passe (haché)
 * sont enregistrés dans la table utilisateur.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idadmin;

    @NotBlank(message = "Le nom est obligatoire")
    private String nomadmin;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenomadmin;

    @Email(message = "Email invalide")
    private String emailadmin;

    // Compte de connexion (login + mot de passe)
    @OneToOne
    @JoinColumn(name = "id_utilisateur")
    @JsonIgnore
    private Utilisateur utilisateur;
}
