package com.cabinet.dao;

import com.cabinet.entities.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {
    Optional<Specialite> findByNomspecialiteIgnoreCase(String nomspecialite);
}
