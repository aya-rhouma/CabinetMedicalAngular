package com.cabinet.service.interfaces;

import com.cabinet.entities.Patient;

import java.util.List;

public interface IPatientService {
    List<Patient> findAll();
    List<Patient> searchByNom(String nom);
    Patient findById(Long id);
    Patient save(Patient patient);
    Patient update(Long id, Patient patient);
    void delete(Long id);
}
