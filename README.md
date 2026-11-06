# Gestion de cabinet médical

Application web de gestion d'un cabinet médical, développée avec Spring Boot, Angular et MySQL. Elle propose des parcours adaptés aux visiteurs, aux patients, aux médecins et à l'administrateur.

Dépôt GitHub : [aya-rhouma/CabinetMedicalAngular](https://github.com/aya-rhouma/CabinetMedicalAngular)

## Fonctionnalités

| Profil | Fonctionnalités |
|---|---|
| Visiteur | Consulter les médecins, rechercher par nom ou spécialité, s'inscrire comme patient et se connecter |
| Patient | Prendre ou annuler un rendez-vous, consulter ses rendez-vous et leur statut, voir les informations de consultation, modifier son profil ou supprimer son compte |
| Médecin | Consulter ses rendez-vous, modifier leur statut, saisir ou modifier une consultation avec récapitulatif, ordonnance et certificat, gérer son profil |
| Administrateur | Consulter les statistiques, créer/modifier/supprimer des médecins et gérer les spécialités |
| Utilisateur connecté | Modifier son profil et son mot de passe |

## Technologies et arborescence

- Backend : Java 17+, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security et JWT.
- Frontend : Angular 20 et TypeScript.
- Base de données : MySQL.
- Collection Postman : `postman/CabinetMedical.postman_collection.json`.

```text
src/main/java/com/cabinet/
  controller/    Endpoints REST
  service/       Règles métier et interfaces de service
  dao/           Repositories Spring Data JPA
  entities/      Entités persistées
  dto/           Objets de requête et de réponse
  security/      Configuration Spring Security, JWT et initialisation admin
  exception/     Gestion centralisée des erreurs
src/main/resources/application.properties
cabinet-front/   Application Angular
postman/         Collection de requêtes API
 docs/           Schéma SQL, diagramme de classes et rapport
```

## Prérequis

- JDK 17 ou plus récent (le projet cible Java 17 dans `pom.xml`).
- Maven 3.8+.
- MySQL 8, démarré et accessible.
- Node.js/npm compatibles avec Angular 20.

## Configuration MySQL

Le fichier `src/main/resources/application.properties` utilise les variables d'environnement ci-dessous. Ne mettez pas de mot de passe personnel directement dans ce fichier ni dans Git.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cabinet_medical_v2?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

`createDatabaseIfNotExist=true` crée la base si l'utilisateur MySQL possède les droits requis. `allowPublicKeyRetrieval=true` facilite la connexion de développement local; pour un déploiement, configurez TLS et un compte MySQL dédié aux privilèges limités.

### IntelliJ IDEA

Dans **Run → Edit Configurations… → Environment variables**, renseignez les identifiants réels de votre MySQL, par exemple :

```text
DB_USERNAME=root; DB_PASSWORD=votre_mot_de_passe
```

Si `root` n'a pas de mot de passe, utilisez `DB_PASSWORD=`. `Access denied for user` signifie que les identifiants sont incorrects ou que ce compte n'est pas autorisé à se connecter depuis `localhost`.

### PowerShell

Pour lancer le backend depuis la racine du dépôt dans le terminal courant :

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "votre_mot_de_passe"
mvn spring-boot:run
```

Pour un environnement partagé, fournissez également une clé JWT secrète d'au moins 32 caractères via `APP_JWT_SECRET`; ne réutilisez pas la clé de développement par défaut.

## Démarrage

### Backend

Depuis la racine du dépôt, après avoir démarré MySQL et configuré `DB_USERNAME` et `DB_PASSWORD` :

```bash
mvn spring-boot:run
```

L'API est disponible sur `http://localhost:8081/api`. Hibernate utilise `ddl-auto=update` pour mettre à jour le schéma au démarrage. Le schéma de référence est dans `docs/schema_bd.sql`.

### Frontend

Dans un deuxième terminal :

```bash
cd cabinet-front
npm ci
npm start
```

Angular est disponible sur `http://localhost:4200`. L'adresse de l'API est actuellement définie dans `cabinet-front/src/app/api.service.ts` et pointe vers `http://localhost:8081/api`.

## Compte administrateur initial

Au premier démarrage, l'application initialise `admin@cabinet.tn` avec le mot de passe temporaire `Admin123!`. Changez-le dès la première connexion. Ces identifiants sont définis dans `DataInitializer.java`; ils sont destinés au développement/TP et ne doivent pas être conservés en production.

Les patients s'inscrivent eux-mêmes; les comptes médecins sont créés par l'administrateur.

## Routes principales

| Zone | Routes |
|---|---|
| Public | `GET /api/public/medecins`, `GET /api/public/specialites` |
| Authentification | `POST /api/auth/login`, `POST /api/auth/register`, `GET/PUT /api/auth/profile`, `POST /api/auth/change-password` |
| Patient | `GET/POST /api/patient/rendezvous`, `PUT /api/patient/rendezvous/{id}/annuler`, `POST /api/patient/supprimer-compte` |
| Médecin | `GET /api/medecin/rendezvous`, `PUT /api/medecin/rendezvous/{id}/statut`, `POST/PUT /api/medecin/rendezvous/{id}/consultation` |
| Admin | `GET /api/admin/stats`, `/api/admin/medecins`, `/api/admin/patients`, `/api/admin/specialites` (opérations selon le rôle et la méthode HTTP) |
| CRUD admin | `/api/specialites`, `/api/medecins`, `/api/patients`, `/api/rendezvous`, `/api/consultations` |

La collection Postman comprend les requêtes d'authentification et les scénarios par rôle. Connectez-vous d'abord pour obtenir un jeton avant d'appeler une route protégée.

## Règles métier

- Un même créneau médecin/date/heure ne peut être réservé qu'une fois; l'annulation libère le créneau.
- Le patient ne peut réserver que dans le futur.
- Le médecin ne modifie que ses propres rendez-vous.
- Seul un rendez-vous programmé peut changer de statut; la création d'une consultation marque le rendez-vous comme passé.
- Les codes d'erreur usuels sont `400` (requête invalide), `401` (non authentifié), `403` (rôle insuffisant), `404` (introuvable) et `409` (conflit métier).

## Documentation

- Diagramme de classes : `docs/diagramme_classes.png` (source : `docs/diagramme_classes.mmd`).
- Schéma de la base : `docs/schema_bd.sql`.
- Mini-rapport : `docs/rapport_tp5.tex`; les captures sont dans `docs/captures/`.
