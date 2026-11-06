package com.cabinet.service.impliments;

import com.cabinet.dao.AdminRepository;
import com.cabinet.dao.MedecinRepository;
import com.cabinet.dao.PatientRepository;
import com.cabinet.dao.SpecialiteRepository;
import com.cabinet.dao.UtilisateurRepository;
import com.cabinet.dto.AuthResponse;
import com.cabinet.dto.ChangePasswordRequest;
import com.cabinet.dto.LoginRequest;
import com.cabinet.dto.ProfileRequest;
import com.cabinet.dto.ProfileResponse;
import com.cabinet.dto.RegisterRequest;
import com.cabinet.entities.Admin;
import com.cabinet.entities.Medecin;
import com.cabinet.entities.Patient;
import com.cabinet.entities.Role;
import com.cabinet.entities.Utilisateur;
import com.cabinet.exception.BadRequestException;
import com.cabinet.exception.BusinessException;
import com.cabinet.exception.ResourceNotFoundException;
import com.cabinet.exception.UnauthorizedException;
import com.cabinet.security.JwtService;
import com.cabinet.service.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements IAuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final AdminRepository adminRepository;
    private final SpecialiteRepository specialiteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(normalize(request.email()))
                .orElseThrow(() -> new UnauthorizedException("Email ou mot de passe incorrect."));
        if (!passwordEncoder.matches(request.motDePasse(), utilisateur.getMotDePasse())) {
            throw new UnauthorizedException("Email ou mot de passe incorrect.");
        }
        return toResponse(utilisateur);
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        if (utilisateurRepository.existsByEmail(email)) {
            throw new BusinessException("Cet email est déjà utilisé.");
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        utilisateur.setRole(Role.PATIENT);
        utilisateurRepository.save(utilisateur);

        Patient patient = new Patient();
        patient.setNompatient(request.nom().trim());
        patient.setPrenompatient(request.prenom().trim());
        patient.setAdrpatient(request.adresse());
        patient.setEmailpatient(email);
        patient.setUtilisateur(utilisateur);
        patientRepository.save(patient);

        return toResponse(utilisateur);
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        Utilisateur utilisateur = findUser(email);
        if (!passwordEncoder.matches(request.ancienMotDePasse(), utilisateur.getMotDePasse())) {
            throw new BadRequestException("L'ancien mot de passe est incorrect.");
        }
        if (request.ancienMotDePasse().equals(request.nouveauMotDePasse())) {
            throw new BadRequestException("Le nouveau mot de passe doit être différent de l'ancien.");
        }
        utilisateur.setMotDePasse(passwordEncoder.encode(request.nouveauMotDePasse()));
        utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String email) {
        Utilisateur utilisateur = findUser(email);
        return switch (utilisateur.getRole()) {
            case PATIENT -> {
                Patient p = patientRepository.findByUtilisateur_Email(email)
                        .orElseThrow(() -> new ResourceNotFoundException("Profil patient introuvable."));
                yield new ProfileResponse(Role.PATIENT, p.getNompatient(), p.getPrenompatient(),
                        p.getAdrpatient(), email, null);
            }
            case MEDECIN -> {
                Medecin m = medecinRepository.findByUtilisateur_Email(email)
                        .orElseThrow(() -> new ResourceNotFoundException("Profil médecin introuvable."));
                yield new ProfileResponse(Role.MEDECIN, m.getNom(), m.getPrenom(), m.getAdr(), email,
                        m.getSpecialite().getIdspec());
            }
            case ADMIN -> {
                Admin a = adminRepository.findByUtilisateur_Email(email).orElse(null);
                yield a == null
                        ? new ProfileResponse(Role.ADMIN, "Admin", "Cabinet", null, email, null)
                        : new ProfileResponse(Role.ADMIN, a.getNomadmin(), a.getPrenomadmin(), null, email, null);
            }
        };
    }

    @Override
    public AuthResponse updateProfile(String currentEmail, ProfileRequest request) {
        Utilisateur utilisateur = findUser(currentEmail);
        String newEmail = normalize(request.email());

        utilisateurRepository.findByEmail(newEmail)
                .filter(autre -> !autre.getId().equals(utilisateur.getId()))
                .ifPresent(autre -> { throw new BusinessException("Cet email est déjà utilisé."); });

        switch (utilisateur.getRole()) {
            case PATIENT -> {
                Patient p = patientRepository.findByUtilisateur_Email(currentEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("Profil patient introuvable."));
                p.setNompatient(request.nom().trim());
                p.setPrenompatient(request.prenom().trim());
                p.setAdrpatient(request.adresse());
                p.setEmailpatient(newEmail);
                patientRepository.save(p);
            }
            case MEDECIN -> {
                Medecin m = medecinRepository.findByUtilisateur_Email(currentEmail)
                        .orElseThrow(() -> new ResourceNotFoundException("Profil médecin introuvable."));
                m.setNom(request.nom().trim());
                m.setPrenom(request.prenom().trim());
                m.setAdr(request.adresse());
                m.setEmail(newEmail);
                if (request.idspec() != null) {
                    m.setSpecialite(specialiteRepository.findById(request.idspec())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Spécialité introuvable : " + request.idspec())));
                }
                medecinRepository.save(m);
            }
            case ADMIN -> {
                Admin a = adminRepository.findByUtilisateur_Email(currentEmail).orElseGet(() -> {
                    Admin nouveau = new Admin();
                    nouveau.setUtilisateur(utilisateur);
                    return nouveau;
                });
                a.setNomadmin(request.nom().trim());
                a.setPrenomadmin(request.prenom().trim());
                a.setEmailadmin(newEmail);
                adminRepository.save(a);
            }
        }

        utilisateur.setEmail(newEmail);
        utilisateurRepository.save(utilisateur);
        // nouveau jeton : le login (email) est le sujet du jeton
        return toResponse(utilisateur);
    }

    private Utilisateur findUser(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
    }

    private AuthResponse toResponse(Utilisateur utilisateur) {
        return new AuthResponse(
                jwtService.generate(utilisateur),
                utilisateur.getRole(),
                utilisateur.getEmail(),
                nomComplet(utilisateur));
    }

    private String nomComplet(Utilisateur utilisateur) {
        String email = utilisateur.getEmail();
        return switch (utilisateur.getRole()) {
            case MEDECIN -> medecinRepository.findByUtilisateur_Email(email)
                    .map(m -> "Dr " + m.getPrenom() + " " + m.getNom())
                    .orElse(email);
            case PATIENT -> patientRepository.findByUtilisateur_Email(email)
                    .map(p -> p.getPrenompatient() + " " + p.getNompatient())
                    .orElse(email);
            case ADMIN -> adminRepository.findByUtilisateur_Email(email)
                    .map(a -> a.getPrenomadmin() + " " + a.getNomadmin())
                    .orElse("Administrateur");
        };
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
