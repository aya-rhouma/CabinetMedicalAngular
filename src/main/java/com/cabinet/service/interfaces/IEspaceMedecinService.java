package com.cabinet.service.interfaces;

import com.cabinet.dto.ConsultationRequest;
import com.cabinet.entities.Rendezvous;
import com.cabinet.entities.StatutRdv;

import java.util.List;

public interface IEspaceMedecinService {
    List<Rendezvous> mesRendezvous(String email);
    Rendezvous changerStatut(String email, Long idrdv, StatutRdv statut);
    Rendezvous ajouterConsultation(String email, Long idrdv, ConsultationRequest request);
    Rendezvous modifierConsultation(String email, Long idrdv, ConsultationRequest request);
}
