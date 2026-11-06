import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AuthResponse, ConsultationBody, Medecin, NewMedecinBody, Patient,
  Profile, ProfileBody, RegisterBody, Rendezvous, Specialite, Stats, StatutRdv, UpdateMedecinBody,
} from './models';

export const API_URL = 'http://localhost:8081/api';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);

  // ----- Authentification -----
  login(email: string, motDePasse: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_URL}/auth/login`, { email, motDePasse });
  }
  register(body: RegisterBody): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_URL}/auth/register`, body);
  }
  changePassword(ancienMotDePasse: string, nouveauMotDePasse: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${API_URL}/auth/change-password`,
      { ancienMotDePasse, nouveauMotDePasse });
  }

  getProfile(): Observable<Profile> {
    return this.http.get<Profile>(`${API_URL}/auth/profile`);
  }
  updateProfile(body: ProfileBody): Observable<AuthResponse> {
    return this.http.put<AuthResponse>(`${API_URL}/auth/profile`, body);
  }

  // ----- Public (page d'accueil) -----
  publicMedecins(): Observable<Medecin[]> {
    return this.http.get<Medecin[]>(`${API_URL}/public/medecins`);
  }
  publicSpecialites(): Observable<Specialite[]> {
    return this.http.get<Specialite[]>(`${API_URL}/public/specialites`);
  }

  // ----- Espace patient -----
  patientRdv(): Observable<Rendezvous[]> {
    return this.http.get<Rendezvous[]>(`${API_URL}/patient/rendezvous`);
  }
  prendreRdv(body: { idMedecin: number; daterdv: string; heurerdv: string }): Observable<Rendezvous> {
    return this.http.post<Rendezvous>(`${API_URL}/patient/rendezvous`, body);
  }
  annulerRdv(id: number): Observable<Rendezvous> {
    return this.http.put<Rendezvous>(`${API_URL}/patient/rendezvous/${id}/annuler`, {});
  }
  supprimerCompte(motDePasse: string): Observable<void> {
    return this.http.post<void>(`${API_URL}/patient/supprimer-compte`, { motDePasse });
  }

  // ----- Espace médecin -----
  medecinRdv(): Observable<Rendezvous[]> {
    return this.http.get<Rendezvous[]>(`${API_URL}/medecin/rendezvous`);
  }
  changerStatut(id: number, statut: StatutRdv): Observable<Rendezvous> {
    return this.http.put<Rendezvous>(`${API_URL}/medecin/rendezvous/${id}/statut`, { statut });
  }
  ajouterConsultation(id: number, body: ConsultationBody): Observable<Rendezvous> {
    return this.http.post<Rendezvous>(`${API_URL}/medecin/rendezvous/${id}/consultation`, body);
  }
  modifierConsultation(id: number, body: ConsultationBody): Observable<Rendezvous> {
    return this.http.put<Rendezvous>(`${API_URL}/medecin/rendezvous/${id}/consultation`, body);
  }

  // ----- Espace admin -----
  stats(): Observable<Stats> {
    return this.http.get<Stats>(`${API_URL}/admin/stats`);
  }
  adminMedecins(): Observable<Medecin[]> {
    return this.http.get<Medecin[]>(`${API_URL}/admin/medecins`);
  }
  creerMedecin(body: NewMedecinBody): Observable<Medecin> {
    return this.http.post<Medecin>(`${API_URL}/admin/medecins`, body);
  }
  modifierMedecin(id: number, body: UpdateMedecinBody): Observable<Medecin> {
    return this.http.put<Medecin>(`${API_URL}/admin/medecins/${id}`, body);
  }
  supprimerMedecin(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/admin/medecins/${id}`);
  }
  adminPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${API_URL}/admin/patients`);
  }
  adminSpecialites(): Observable<Specialite[]> {
    return this.http.get<Specialite[]>(`${API_URL}/admin/specialites`);
  }
  creerSpecialite(nomspecialite: string): Observable<Specialite> {
    return this.http.post<Specialite>(`${API_URL}/admin/specialites`, { nomspecialite });
  }
  modifierSpecialite(id: number, nomspecialite: string): Observable<Specialite> {
    return this.http.put<Specialite>(`${API_URL}/admin/specialites/${id}`, { nomspecialite });
  }
  supprimerSpecialite(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/admin/specialites/${id}`);
  }
}
