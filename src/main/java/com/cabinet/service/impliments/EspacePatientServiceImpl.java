package com.cabinet.service.impliments;

import com.cabinet.dao.MedecinRepository;
import com.cabinet.dao.PatientRepository;
import com.cabinet.dao.RendezvousRepository;
import com.cabinet.dao.UtilisateurRepository;
import com.cabinet.entities.Utilisateur;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.cabinet.dto.RdvPatientRequest;
import com.cabinet.entities.Medecin;
import com.cabinet.entities.Patient;
import com.cabinet.entities.Rendezvous;
import com.cabinet.entities.StatutRdv;
import com.cabinet.exception.BadRequestException;
import com.cabinet.exception.BusinessException;
import com.cabinet.exception.ResourceNotFoundException;
import com.cabinet.service.interfaces.IEspacePatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EspacePatientServiceImpl implements IEspacePatientService {

    private final RendezvousRepository rendezvousRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<Rendezvous> mesRendezvous(String email) {
        Patient patient = patient(email);
        return rendezvousRepository.findByPatient_IdpatientOrderByDaterdvDescHeurerdvDesc(patient.getIdpatient());
    }

    @Override
    public Rendezvous prendreRendezvous(String email, RdvPatientRequest request) {
        Patient patient = patient(email);
        Medecin medecin = medecinRepository.findById(request.idMedecin())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable : " + request.idMedecin()));

        LocalDate today = LocalDate.now();
        if (request.daterdv().isBefore(today)
                || (request.daterdv().isEqual(today) && request.heurerdv().isBefore(LocalTime.now()))) {
            throw new BadRequestException("La date et l'heure du rendez-vous doivent être dans le futur.");
        }
        if (rendezvousRepository.existsByMedecin_IdAndDaterdvAndHeurerdvAndStatutNot(
                medecin.getId(), request.daterdv(), request.heurerdv(), StatutRdv.ANNULE)) {
            throw new BusinessException("Ce créneau est déjà réservé pour ce médecin. Choisissez une autre heure.");
        }

        Rendezvous rdv = new Rendezvous();
        rdv.setDaterdv(request.daterdv());
        rdv.setHeurerdv(request.heurerdv());
        rdv.setStatut(StatutRdv.PROGRAMME);
        rdv.setMedecin(medecin);
        rdv.setPatient(patient);
        return rendezvousRepository.save(rdv);
    }

    @Override
    public Rendezvous annuler(String email, Long idrdv) {
        Patient patient = patient(email);
        Rendezvous rdv = rendezvousRepository.findById(idrdv)
                .filter(r -> r.getPatient().getIdpatient().equals(patient.getIdpatient()))
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable : " + idrdv));
        if (rdv.getStatut() != StatutRdv.PROGRAMME) {
            throw new BusinessException("Seul un rendez-vous programmé peut être annulé.");
        }
        rdv.setStatut(StatutRdv.ANNULE);
        return rendezvousRepository.save(rdv);
    }

    @Override
    public void supprimerCompte(String email, String motDePasse) {
        Patient patient = patient(email);
        Utilisateur compte = patient.getUtilisateur();
        // 400 (et non 401) pour ne pas déconnecter l'utilisateur sur une simple faute de frappe
        if (compte == null || !passwordEncoder.matches(motDePasse, compte.getMotDePasse())) {
            throw new BadRequestException("Mot de passe incorrect.");
        }
        // les consultations disparaissent avec leur rendez-vous (cascade)
        rendezvousRepository.deleteAll(rendezvousRepository.findByPatient_Idpatient(patient.getIdpatient()));
        patientRepository.delete(patient);
        utilisateurRepository.delete(compte);
    }

    private Patient patient(String email) {
        return patientRepository.findByUtilisateur_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profil patient introuvable."));
    }
}
