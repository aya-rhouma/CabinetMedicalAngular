package com.cabinet.service.interfaces;

import com.cabinet.dto.CreateMedecinRequest;
import com.cabinet.dto.UpdateMedecinRequest;
import com.cabinet.entities.Medecin;
import com.cabinet.entities.Patient;

import java.util.List;
import java.util.Map;

public interface IAdminService {
    Map<String, Object> statistiques();
    Medecin creerMedecin(CreateMedecinRequest request);
    /** Modifie tous les attributs du médecin sauf son mot de passe. */
    Medecin modifierMedecin(Long id, UpdateMedecinRequest request);
    /** Supprime le médecin, son compte, ses rendez-vous et leurs consultations. */
    void supprimerMedecin(Long id);
    List<Patient> patients();
}
