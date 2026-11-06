package com.cabinet.service.impliments;

import com.cabinet.dao.MedecinRepository;
import com.cabinet.dao.SpecialiteRepository;
import com.cabinet.entities.Medecin;
import com.cabinet.entities.Specialite;
import com.cabinet.exception.ResourceNotFoundException;
import com.cabinet.service.interfaces.IMedecinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedecinServiceImpl implements IMedecinService {

    private final MedecinRepository medecinRepository;
    private final SpecialiteRepository specialiteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> findAll() {
        return medecinRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> searchByNom(String nom) {
        return medecinRepository.findByNomContainingIgnoreCase(nom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> findBySpecialite(Long idspec) {
        return medecinRepository.findBySpecialite_Idspec(idspec);
    }

    @Override
    @Transactional(readOnly = true)
    public Medecin findById(Long id) {
        return medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable : " + id));
    }

    @Override
    public Medecin save(Medecin medecin) {
        medecin.setId(null);
        medecin.setSpecialite(resolveSpecialite(medecin.getSpecialite()));
        return medecinRepository.save(medecin);
    }

    @Override
    public Medecin update(Long id, Medecin medecin) {
        Medecin existing = findById(id);
        existing.setNom(medecin.getNom());
        existing.setPrenom(medecin.getPrenom());
        existing.setAdr(medecin.getAdr());
        existing.setEmail(medecin.getEmail());
        existing.setSpecialite(resolveSpecialite(medecin.getSpecialite()));
        return medecinRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        medecinRepository.delete(findById(id));
    }

    private Specialite resolveSpecialite(Specialite s) {
        if (s == null || s.getIdspec() == null) {
            throw new ResourceNotFoundException("Spécialité manquante : fournir {\"specialite\": {\"idspec\": X}}");
        }
        return specialiteRepository.findById(s.getIdspec())
                .orElseThrow(() -> new ResourceNotFoundException("Spécialité introuvable : " + s.getIdspec()));
    }
}
