package com.cabinet.service.interfaces;

import com.cabinet.entities.Medecin;

import java.util.List;

public interface IMedecinService {
    List<Medecin> findAll();
    List<Medecin> searchByNom(String nom);
    List<Medecin> findBySpecialite(Long idspec);
    Medecin findById(Long id);
    Medecin save(Medecin medecin);
    Medecin update(Long id, Medecin medecin);
    void delete(Long id);
}
