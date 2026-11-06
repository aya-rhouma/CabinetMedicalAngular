package com.cabinet.dao;

import com.cabinet.entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByRendezvous_Idrdv(Long idrdv);
    boolean existsByRendezvous_Idrdv(Long idrdv);
}
