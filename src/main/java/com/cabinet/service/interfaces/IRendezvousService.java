package com.cabinet.service.interfaces;

import com.cabinet.entities.Rendezvous;

import java.util.List;

public interface IRendezvousService {
    List<Rendezvous> findAll();
    List<Rendezvous> findByMedecin(Long idMedecin);
    List<Rendezvous> findByPatient(Long idPatient);
    Rendezvous findById(Long id);
    Rendezvous save(Rendezvous rendezvous);
    Rendezvous update(Long id, Rendezvous rendezvous);
    void delete(Long id);
}
