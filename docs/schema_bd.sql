-- Schéma MySQL de référence.
-- Il est créé automatiquement par Hibernate (spring.jpa.hibernate.ddl-auto=update) :
-- ce fichier sert de documentation et n'a pas besoin d'être exécuté.

-- Comptes de connexion des trois rôles (patient, médecin, admin)
CREATE TABLE utilisateur (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  email         VARCHAR(255) NOT NULL UNIQUE,          -- login
  mot_de_passe  VARCHAR(255) NOT NULL,                 -- haché (BCrypt), jamais en clair
  role          VARCHAR(255) NOT NULL                  -- ADMIN | MEDECIN | PATIENT
);

CREATE TABLE specialite (
  idspec          BIGINT AUTO_INCREMENT PRIMARY KEY,
  nomspecialite   VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE admin (
  idadmin         BIGINT AUTO_INCREMENT PRIMARY KEY,
  nomadmin        VARCHAR(255),
  prenomadmin     VARCHAR(255),
  emailadmin      VARCHAR(255),
  id_utilisateur  BIGINT UNIQUE,
  FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id)
);

CREATE TABLE medecin (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom             VARCHAR(255),
  prenom          VARCHAR(255),
  adr             VARCHAR(255),
  email           VARCHAR(255),
  idspec          BIGINT NOT NULL,
  id_utilisateur  BIGINT UNIQUE,
  FOREIGN KEY (idspec) REFERENCES specialite(idspec),
  FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id)
);

CREATE TABLE patient (
  idpatient       BIGINT AUTO_INCREMENT PRIMARY KEY,
  nompatient      VARCHAR(255),
  prenompatient   VARCHAR(255),
  adrpatient      VARCHAR(255),
  emailpatient    VARCHAR(255),
  id_utilisateur  BIGINT UNIQUE,
  FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id)
);

CREATE TABLE rendezvous (
  idrdv       BIGINT AUTO_INCREMENT PRIMARY KEY,
  daterdv     DATE,
  heurerdv    TIME,
  statut      VARCHAR(255),                            -- PROGRAMME | PASSE | ANNULE
  id_medecin  BIGINT NOT NULL,
  id_patient  BIGINT NOT NULL,
  FOREIGN KEY (id_medecin) REFERENCES medecin(id),
  FOREIGN KEY (id_patient) REFERENCES patient(idpatient)
);

CREATE TABLE consultation (
  idcons      BIGINT AUTO_INCREMENT PRIMARY KEY,
  datecons    DATE,
  recapcons   VARCHAR(2000),
  ordonnance  VARCHAR(4000),
  certificat  VARCHAR(4000),
  idrdv       BIGINT NOT NULL UNIQUE,                  -- 1..1 avec rendezvous
  FOREIGN KEY (idrdv) REFERENCES rendezvous(idrdv)
);
