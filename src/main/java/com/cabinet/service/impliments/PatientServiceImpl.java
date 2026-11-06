package com.cabinet.service.impliments;

import com.cabinet.dao.PatientRepository;
import com.cabinet.entities.Patient;
import com.cabinet.exception.ResourceNotFoundException;
import com.cabinet.service.interfaces.IPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements IPatientService {

    private final PatientRepository patientRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> searchByNom(String nom) {
        return patientRepository.findByNompatientContainingIgnoreCase(nom);
    }

    @Override
    @Transactional(readOnly = true)
    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable : " + id));
    }

    @Override
    public Patient save(Patient patient) {
        patient.setIdpatient(null);
        return patientRepository.save(patient);
    }

    @Override
    public Patient update(Long id, Patient patient) {
        Patient existing = findById(id);
        existing.setNompatient(patient.getNompatient());
        existing.setPrenompatient(patient.getPrenompatient());
        existing.setAdrpatient(patient.getAdrpatient());
        existing.setEmailpatient(patient.getEmailpatient());
        return patientRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        patientRepository.delete(findById(id));
    }
}
