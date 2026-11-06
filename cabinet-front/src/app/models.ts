export type Role = 'ADMIN' | 'MEDECIN' | 'PATIENT';
export type StatutRdv = 'PROGRAMME' | 'PASSE' | 'ANNULE';

export interface Specialite {
  idspec: number;
  nomspecialite: string;
}

export interface Medecin {
  id: number;
  nom: string;
  prenom: string;
  adr?: string;
  email?: string;
  specialite: Specialite;
}

export interface Patient {
  idpatient: number;
  nompatient: string;
  prenompatient: string;
  adrpatient?: string;
  emailpatient?: string;
}

export interface Consultation {
  idcons: number;
  datecons: string;
  recapcons?: string | null;
  ordonnance?: string | null;
  certificat?: string | null;
}

export interface Rendezvous {
  idrdv: number;
  daterdv: string;   // yyyy-MM-dd
  heurerdv: string;  // HH:mm:ss
  statut: StatutRdv | null;
  medecin: Medecin;
  patient: Patient;
  consultation?: Consultation | null;
}

export interface AuthResponse {
  token: string;
  role: Role;
  email: string;
  nomComplet: string;
}

export interface Stats {
  totaux: Record<string, number>;
  parStatut: Record<string, number>;
  parSpecialite: Record<string, number>;
  parMedecin: Record<string, number>;
  parMois: Record<string, number>;
}

export interface RegisterBody {
  nom: string;
  prenom: string;
  adresse: string;
  email: string;
  motDePasse: string;
}

export interface NewMedecinBody {
  nom: string;
  prenom: string;
  adr: string;
  email: string;
  motDePasse: string;
  idspec: number;
}

export interface Profile {
  role: Role;
  nom: string;
  prenom: string;
  adresse: string | null;
  email: string;
  idspec: number | null;
}

export interface ProfileBody {
  nom: string;
  prenom: string;
  adresse: string;
  email: string;
  idspec: number | null;
}

export interface UpdateMedecinBody {
  nom: string;
  prenom: string;
  adr: string;
  email: string;
  idspec: number;
}

export interface ConsultationBody {
  datecons: string;
  recapcons: string;
  ordonnance: string;
  certificat: string;
}
