package com.cabinet.security;

import com.cabinet.dao.AdminRepository;
import com.cabinet.dao.UtilisateurRepository;
import com.cabinet.entities.Admin;
import com.cabinet.entities.Role;
import com.cabinet.entities.Utilisateur;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Crée le compte administrateur (table utilisateur + table admin) au premier démarrage. */
@Slf4j
@Configuration
public class DataInitializer {

    private static final String ADMIN_EMAIL = "admin@cabinet.tn";
    private static final String ADMIN_PASSWORD = "Admin123!";

    @Bean
    public CommandLineRunner initAdmin(UtilisateurRepository utilisateurRepository,
                                       AdminRepository adminRepository,
                                       PasswordEncoder encoder) {
        return args -> {
            Utilisateur compte = utilisateurRepository.findByEmail(ADMIN_EMAIL).orElse(null);

            if (compte == null && !utilisateurRepository.existsByRole(Role.ADMIN)) {
                compte = new Utilisateur();
                compte.setEmail(ADMIN_EMAIL);
                compte.setMotDePasse(encoder.encode(ADMIN_PASSWORD));
                compte.setRole(Role.ADMIN);
                compte = utilisateurRepository.save(compte);
                log.info("Compte admin créé : {} / {} (à changer après la première connexion)",
                        ADMIN_EMAIL, ADMIN_PASSWORD);
            }

            // fiche admin liée au compte (aussi pour un compte créé par une version précédente)
            if (compte != null && compte.getRole() == Role.ADMIN
                    && adminRepository.findByUtilisateur_Email(compte.getEmail()).isEmpty()) {
                Admin admin = new Admin();
                admin.setNomadmin("Admin");
                admin.setPrenomadmin("Cabinet");
                admin.setEmailadmin(compte.getEmail());
                admin.setUtilisateur(compte);
                adminRepository.save(admin);
            }
        };
    }
}
