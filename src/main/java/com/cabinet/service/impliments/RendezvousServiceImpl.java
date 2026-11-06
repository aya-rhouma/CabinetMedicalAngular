package com.cabinet.service.impliments;

import com.cabinet.dao.MedecinRepository;
import com.cabinet.dao.PatientRepository;
import com.cabinet.dao.RendezvousRepository;
import com.cabinet.entities.Medecin;
import com.cabinet.entities.Patient;
import com.cabinet.entities.Rendezvous;
import com.cabinet.exception.BusinessException;
import com.cabinet.exception.ResourceNotFoundException;
import com.cabinet.service.interfaces.IRendezvousService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RendezvousServiceImpl implements IRendezvousService {

    private final RendezvousRepository rendezvousRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Rendezvous> findAll() {
        return rendezvousRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rendezvous> findByMedecin(Long idMedecin) {
        return rendezvousRepository.findByMedecin_Id(idMedecin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rendezvous> findByPatient(Long idPatient) {
        return rendezvousRepository.findByPatient_Idpatient(idPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public Rendezvous findById(Long id) {
        return rendezvousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable : " + id));
    }

    @Override
    public Rendezvous save(Rendezvous rdv) {
        rdv.setIdrdv(null);
        rdv.setConsultation(null);
        rdv.setMedecin(resolveMedecin(rdv.getMedecin()));
        rdv.setPatient(resolvePatient(rdv.getPatient()));
        if (rendezvousRepository.existsByMedecin_IdAndDaterdvAndHeurerdv(
                rdv.getMedecin().getId(), rdv.getDaterdv(), rdv.getHeurerdv())) {
            throw new BusinessException("Ce médecin a déjà un rendez-vous à cette date et heure.");
        }
        return rendezvousRepository.save(rdv);
    }

    @Override
    public Rendezvous update(Long id, Rendezvous rdv) {
        Rendezvous existing = findById(id);
        Medecin medecin = resolveMedecin(rdv.getMedecin());
        boolean changed = !existing.getMedecin().getId().equals(medecin.getId())
                || !existing.getDaterdv().equals(rdv.getDaterdv())
                || !existing.getHeurerdv().equals(rdv.getHeurerdv());
        if (changed && rendezvousRepository.existsByMedecin_IdAndDaterdvAndHeurerdv(
                medecin.getId(), rdv.getDaterdv(), rdv.getHeurerdv())) {
            throw new BusinessException("Ce médecin a déjà un rendez-vous à cette date et heure.");
        }
        existing.setDaterdv(rdv.getDaterdv());
        existing.setHeurerdv(rdv.getHeurerdv());
        existing.setMedecin(medecin);
        existing.setPatient(resolvePatient(rdv.getPatient()));
        return rendezvousRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        rendezvousRepository.delete(findById(id));
    }

    private Medecin resolveMedecin(Medecin m) {
        if (m == null || m.getId() == null) {
            throw new ResourceNotFoundException("Médecin manquant : fournir {\"medecin\": {\"id\": X}}");
        }
        return medecinRepository.findById(m.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable : " + m.getId()));
    }

    private Patient resolvePatient(Patient p) {
        if (p == null || p.getIdpatient() == null) {
            throw new ResourceNotFoundException("Patient manquant : fournir {\"patient\": {\"idpatient\": X}}");
        }
        return patientRepository.findById(p.getIdpatient())
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable : " + p.getIdpatient()));
    }
}
