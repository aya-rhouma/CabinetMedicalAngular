package com.cabinet.dao;

import com.cabinet.entities.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    List<Medecin> findByNomContainingIgnoreCase(String nom);
    List<Medecin> findBySpecialite_Idspec(Long idspec);
    Optional<Medecin> findByUtilisateur_Email(String email);
}
