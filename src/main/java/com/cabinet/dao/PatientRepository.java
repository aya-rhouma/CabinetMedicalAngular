package com.cabinet.dao;

import com.cabinet.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByNompatientContainingIgnoreCase(String nom);
    Optional<Patient> findByUtilisateur_Email(String email);
}
