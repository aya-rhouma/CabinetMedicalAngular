package com.cabinet.service.interfaces;

import com.cabinet.dto.RdvPatientRequest;
import com.cabinet.entities.Rendezvous;

import java.util.List;

public interface IEspacePatientService {
    List<Rendezvous> mesRendezvous(String email);
    Rendezvous prendreRendezvous(String email, RdvPatientRequest request);
    Rendezvous annuler(String email, Long idrdv);
    /** Supprime le compte du patient, son profil, ses rendez-vous et leurs consultations. */
    void supprimerCompte(String email, String motDePasse);
}
