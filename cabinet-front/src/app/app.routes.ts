import { Routes } from '@angular/router';
import { loggedGuard, roleGuard } from './auth.guard';
import { HomeComponent } from './pages/home.component';
import { LoginComponent } from './pages/login.component';
import { PatientSpaceComponent } from './pages/patient-space.component';
import { MedecinSpaceComponent } from './pages/medecin-space.component';
import { AdminSpaceComponent } from './pages/admin-space.component';
import { ProfileComponent } from './pages/profile.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'patient', component: PatientSpaceComponent, canActivate: [roleGuard('PATIENT')] },
  { path: 'medecin', component: MedecinSpaceComponent, canActivate: [roleGuard('MEDECIN')] },
  { path: 'admin', component: AdminSpaceComponent, canActivate: [roleGuard('ADMIN')] },
  { path: 'profil', component: ProfileComponent, canActivate: [loggedGuard] },
  { path: '**', redirectTo: '' },
];
