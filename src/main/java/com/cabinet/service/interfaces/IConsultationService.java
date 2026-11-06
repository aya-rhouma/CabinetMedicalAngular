package com.cabinet.service.interfaces;

import com.cabinet.entities.Consultation;

import java.util.List;

public interface IConsultationService {
    List<Consultation> findAll();
    Consultation findById(Long id);
    Consultation findByRendezvous(Long idrdv);
    Consultation save(Consultation consultation);
    Consultation update(Long id, Consultation consultation);
    void delete(Long id);
}
