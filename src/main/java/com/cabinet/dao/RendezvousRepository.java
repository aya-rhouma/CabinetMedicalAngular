package com.cabinet.dao;

import com.cabinet.entities.Rendezvous;
import com.cabinet.entities.StatutRdv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RendezvousRepository extends JpaRepository<Rendezvous, Long> {
    List<Rendezvous> findByMedecin_Id(Long idMedecin);
    List<Rendezvous> findByPatient_Idpatient(Long idPatient);
    boolean existsByMedecin_IdAndDaterdvAndHeurerdv(Long idMedecin, LocalDate date, LocalTime heure);

    // Espaces patient / médecin : du plus récent au plus ancien
    List<Rendezvous> findByMedecin_IdOrderByDaterdvDescHeurerdvDesc(Long idMedecin);
    List<Rendezvous> findByPatient_IdpatientOrderByDaterdvDescHeurerdvDesc(Long idPatient);

    // Un rendez-vous annulé libère le créneau
    boolean existsByMedecin_IdAndDaterdvAndHeurerdvAndStatutNot(
            Long idMedecin, LocalDate date, LocalTime heure, StatutRdv statut);
}
