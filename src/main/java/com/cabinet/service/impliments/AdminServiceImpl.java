package com.cabinet.service.impliments;

import com.cabinet.dao.ConsultationRepository;
import com.cabinet.dao.MedecinRepository;
import com.cabinet.dao.PatientRepository;
import com.cabinet.dao.RendezvousRepository;
import com.cabinet.dao.SpecialiteRepository;
import com.cabinet.dao.UtilisateurRepository;
import com.cabinet.dto.CreateMedecinRequest;
import com.cabinet.dto.UpdateMedecinRequest;
import com.cabinet.entities.Medecin;
import com.cabinet.entities.Patient;
import com.cabinet.entities.Rendezvous;
import com.cabinet.entities.Role;
import com.cabinet.entities.Specialite;
import com.cabinet.entities.StatutRdv;
import com.cabinet.entities.Utilisateur;
import com.cabinet.exception.BusinessException;
import com.cabinet.exception.ResourceNotFoundException;
import com.cabinet.service.interfaces.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements IAdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final SpecialiteRepository specialiteRepository;
    private final RendezvousRepository rendezvousRepository;
    private final ConsultationRepository consultationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Medecin creerMedecin(CreateMedecinRequest request) {
        String email = request.email().trim().toLowerCase();
        if (utilisateurRepository.existsByEmail(email)) {
            throw new BusinessException("Cet email est déjà utilisé.");
        }
        Specialite specialite = specialiteRepository.findById(request.idspec())
                .orElseThrow(() -> new ResourceNotFoundException("Spécialité introuvable : " + request.idspec()));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        utilisateur.setRole(Role.MEDECIN);
        utilisateurRepository.save(utilisateur);

        Medecin medecin = new Medecin();
        medecin.setNom(request.nom().trim());
        medecin.setPrenom(request.prenom().trim());
        medecin.setAdr(request.adr());
        medecin.setEmail(email);
        medecin.setSpecialite(specialite);
        medecin.setUtilisateur(utilisateur);
        return medecinRepository.save(medecin);
    }

    @Override
    public Medecin modifierMedecin(Long id, UpdateMedecinRequest request) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable : " + id));
        Specialite specialite = specialiteRepository.findById(request.idspec())
                .orElseThrow(() -> new ResourceNotFoundException("Spécialité introuvable : " + request.idspec()));

        String email = request.email().trim().toLowerCase();
        Utilisateur compte = medecin.getUtilisateur();
        utilisateurRepository.findByEmail(email)
                .filter(autre -> compte == null || !autre.getId().equals(compte.getId()))
                .ifPresent(autre -> { throw new BusinessException("Cet email est déjà utilisé."); });

        medecin.setNom(request.nom().trim());
        medecin.setPrenom(request.prenom().trim());
        medecin.setAdr(request.adr());
        medecin.setEmail(email);
        medecin.setSpecialite(specialite);
        if (compte != null) {
            compte.setEmail(email); // le login suit l'email
            utilisateurRepository.save(compte);
        }
        return medecinRepository.save(medecin);
    }

    @Override
    public void supprimerMedecin(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable : " + id));
        // les consultations disparaissent avec leur rendez-vous (cascade)
        rendezvousRepository.deleteAll(rendezvousRepository.findByMedecin_Id(id));
        Utilisateur compte = medecin.getUtilisateur();
        medecinRepository.delete(medecin);
        if (compte != null) {
            utilisateurRepository.delete(compte);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> patients() {
        return patientRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> statistiques() {
        List<Rendezvous> rendezvous = rendezvousRepository.findAll();

        Map<String, Long> totaux = new LinkedHashMap<>();
        totaux.put("medecins", medecinRepository.count());
        totaux.put("patients", patientRepository.count());
        totaux.put("rendezvous", (long) rendezvous.size());
        totaux.put("consultations", consultationRepository.count());

        Map<String, Long> parStatut = new LinkedHashMap<>();
        for (StatutRdv statut : StatutRdv.values()) {
            parStatut.put(statut.name(), 0L);
        }
        Map<String, Long> parMedecin = new LinkedHashMap<>();
        Map<String, Long> parMois = new TreeMap<>();
        for (Rendezvous r : rendezvous) {
            StatutRdv statut = r.getStatut() != null ? r.getStatut() : StatutRdv.PROGRAMME;
            parStatut.merge(statut.name(), 1L, Long::sum);
            parMedecin.merge("Dr " + r.getMedecin().getPrenom() + " " + r.getMedecin().getNom(), 1L, Long::sum);
            parMois.merge(YearMonth.from(r.getDaterdv()).toString(), 1L, Long::sum);
        }

        // top 8 des médecins par nombre de rendez-vous
        Map<String, Long> topMedecins = new LinkedHashMap<>();
        parMedecin.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(8)
                .forEach(e -> topMedecins.put(e.getKey(), e.getValue()));

        // nombre de médecins par spécialité (y compris les spécialités sans médecin)
        Map<String, Long> parSpecialite = new LinkedHashMap<>();
        specialiteRepository.findAll().forEach(s -> parSpecialite.put(s.getNomspecialite(), 0L));
        medecinRepository.findAll()
                .forEach(m -> parSpecialite.merge(m.getSpecialite().getNomspecialite(), 1L, Long::sum));

        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("totaux", totaux);
        resultat.put("parStatut", parStatut);
        resultat.put("parSpecialite", parSpecialite);
        resultat.put("parMedecin", topMedecins);
        resultat.put("parMois", parMois);
        return resultat;
    }
}
