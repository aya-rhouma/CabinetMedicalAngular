package com.cabinet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
public class Specialite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idspec;

    @NotBlank(message = "Le nom de la spécialité est obligatoire")
    @Column(nullable = false, unique = true)
    private String nomspecialite;

    // 1 spécialité -> 1..* médecins
    @OneToMany(mappedBy = "specialite")
    @JsonIgnore
    private List<Medecin> medecins = new ArrayList<>();
}
