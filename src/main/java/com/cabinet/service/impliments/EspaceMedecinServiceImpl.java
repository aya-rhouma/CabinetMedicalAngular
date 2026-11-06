package com.cabinet.service.impliments;

import com.cabinet.dao.ConsultationRepository;
import com.cabinet.dao.MedecinRepository;
import com.cabinet.dao.RendezvousRepository;
import com.cabinet.dto.ConsultationRequest;
import com.cabinet.entities.Consultation;
import com.cabinet.entities.Medecin;
import com.cabinet.entities.Rendezvous;
import com.cabinet.entities.StatutRdv;
import com.cabinet.exception.BadRequestException;
import com.cabinet.exception.BusinessException;
import com.cabinet.exception.ResourceNotFoundException;
import com.cabinet.service.interfaces.IEspaceMedecinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EspaceMedecinServiceImpl implements IEspaceMedecinService {

    private final RendezvousRepository rendezvousRepository;
    private final ConsultationRepository consultationRepository;
    private final MedecinRepository medecinRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Rendezvous> mesRendezvous(String email) {
        Medecin medecin = medecin(email);
        return rendezvousRepository.findByMedecin_IdOrderByDaterdvDescHeurerdvDesc(medecin.getId());
    }

    @Override
    public Rendezvous changerStatut(String email, Long idrdv, StatutRdv statut) {
        Rendezvous rdv = rendezvousDuMedecin(email, idrdv);
        if (statut == StatutRdv.PROGRAMME) {
            throw new BadRequestException("Un rendez-vous ne peut pas revenir à l'état programmé.");
        }
        if (rdv.getStatut() != StatutRdv.PROGRAMME) {
            throw new BusinessException("Seul un rendez-vous programmé peut changer d'état.");
        }
        rdv.setStatut(statut);
        return rendezvousRepository.save(rdv);
    }

    @Override
    public Rendezvous ajouterConsultation(String email, Long idrdv, ConsultationRequest request) {
        Rendezvous rdv = rendezvousDuMedecin(email, idrdv);
        if (rdv.getStatut() == StatutRdv.ANNULE) {
            throw new BusinessException("Impossible d'ajouter une consultation à un rendez-vous annulé.");
        }
        if (consultationRepository.existsByRendezvous_Idrdv(idrdv)) {
            throw new BusinessException("Ce rendez-vous possède déjà une consultation.");
        }

        Consultation consultation = new Consultation();
        consultation.setDatecons(request.datecons() != null ? request.datecons() : LocalDate.now());
        consultation.setRecapcons(request.recapcons());
        consultation.setOrdonnance(blankToNull(request.ordonnance()));
        consultation.setCertificat(blankToNull(request.certificat()));
        consultation.setRendezvous(rdv);
        Consultation saved = consultationRepository.save(consultation);

        // une consultation enregistrée signifie que le rendez-vous a eu lieu
        rdv.setStatut(StatutRdv.PASSE);
        rendezvousRepository.save(rdv);
        rdv.setConsultation(saved);
        return rdv;
    }

    @Override
    public Rendezvous modifierConsultation(String email, Long idrdv, ConsultationRequest request) {
        Rendezvous rdv = rendezvousDuMedecin(email, idrdv);
        Consultation consultation = consultationRepository.findByRendezvous_Idrdv(idrdv)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune consultation pour le rendez-vous : " + idrdv));
        if (request.datecons() != null) {
            consultation.setDatecons(request.datecons());
        }
        consultation.setRecapcons(request.recapcons());
        consultation.setOrdonnance(blankToNull(request.ordonnance()));
        consultation.setCertificat(blankToNull(request.certificat()));
        rdv.setConsultation(consultationRepository.save(consultation));
        return rdv;
    }

    private Medecin medecin(String email) {
        return medecinRepository.findByUtilisateur_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profil médecin introuvable."));
    }

    /** Un médecin ne peut agir que sur ses propres rendez-vous. */
    private Rendezvous rendezvousDuMedecin(String email, Long idrdv) {
        Medecin medecin = medecin(email);
        return rendezvousRepository.findById(idrdv)
                .filter(r -> r.getMedecin().getId().equals(medecin.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable : " + idrdv));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
