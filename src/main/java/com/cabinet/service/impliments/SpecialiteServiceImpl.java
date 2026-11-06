package com.cabinet.service.impliments;

import com.cabinet.dao.MedecinRepository;
import com.cabinet.dao.SpecialiteRepository;
import com.cabinet.entities.Specialite;
import com.cabinet.exception.BusinessException;
import com.cabinet.exception.ResourceNotFoundException;
import com.cabinet.service.interfaces.ISpecialiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialiteServiceImpl implements ISpecialiteService {

    private final SpecialiteRepository specialiteRepository;
    private final MedecinRepository medecinRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Specialite> findAll() {
        return specialiteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Specialite findById(Long id) {
        return specialiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spécialité introuvable : " + id));
    }

    @Override
    public Specialite save(Specialite specialite) {
        String nom = specialite.getNomspecialite().trim();
        specialiteRepository.findByNomspecialiteIgnoreCase(nom).ifPresent(autre -> {
            throw new BusinessException("Cette spécialité existe déjà.");
        });
        specialite.setIdspec(null);
        specialite.setNomspecialite(nom);
        return specialiteRepository.save(specialite);
    }

    @Override
    public Specialite update(Long id, Specialite specialite) {
        Specialite existing = findById(id);
        String nom = specialite.getNomspecialite().trim();
        specialiteRepository.findByNomspecialiteIgnoreCase(nom)
                .filter(autre -> !autre.getIdspec().equals(id))
                .ifPresent(autre -> {
                    throw new BusinessException("Cette spécialité existe déjà.");
                });
        existing.setNomspecialite(nom);
        return specialiteRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Specialite existing = findById(id);
        int nb = medecinRepository.findBySpecialite_Idspec(id).size();
        if (nb > 0) {
            throw new BusinessException("Impossible de supprimer « " + existing.getNomspecialite()
                    + " » : " + nb + " médecin(s) l'utilisent. Modifiez ou supprimez ces médecins d'abord.");
        }
        specialiteRepository.delete(existing);
    }
}
