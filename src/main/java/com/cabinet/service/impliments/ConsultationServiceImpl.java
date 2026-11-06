package com.cabinet.service.impliments;

import com.cabinet.dao.ConsultationRepository;
import com.cabinet.dao.RendezvousRepository;
import com.cabinet.entities.Consultation;
import com.cabinet.entities.Rendezvous;
import com.cabinet.exception.BusinessException;
import com.cabinet.exception.ResourceNotFoundException;
import com.cabinet.service.interfaces.IConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultationServiceImpl implements IConsultationService {

    private final ConsultationRepository consultationRepository;
    private final RendezvousRepository rendezvousRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Consultation> findAll() {
        return consultationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Consultation findById(Long id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation introuvable : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Consultation findByRendezvous(Long idrdv) {
        return consultationRepository.findByRendezvous_Idrdv(idrdv)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune consultation pour le rendez-vous : " + idrdv));
    }

    @Override
    public Consultation save(Consultation consultation) {
        consultation.setIdcons(null);
        Rendezvous rdv = resolveRendezvous(consultation.getRendezvous());
        if (consultationRepository.existsByRendezvous_Idrdv(rdv.getIdrdv())) {
            throw new BusinessException("Ce rendez-vous possède déjà une consultation.");
        }
        consultation.setRendezvous(rdv);
        return consultationRepository.save(consultation);
    }

    @Override
    public Consultation update(Long id, Consultation consultation) {
        Consultation existing = findById(id);
        existing.setDatecons(consultation.getDatecons());
        existing.setRecapcons(consultation.getRecapcons());
        return consultationRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        consultationRepository.delete(findById(id));
    }

    private Rendezvous resolveRendezvous(Rendezvous r) {
        if (r == null || r.getIdrdv() == null) {
            throw new ResourceNotFoundException("Rendez-vous manquant : fournir {\"rendezvous\": {\"idrdv\": X}}");
        }
        return rendezvousRepository.findById(r.getIdrdv())
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable : " + r.getIdrdv()));
    }
}
