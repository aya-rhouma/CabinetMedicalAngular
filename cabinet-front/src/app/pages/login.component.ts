import { Component, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { AuthService } from '../auth.service';
import { AuthResponse, Role } from '../models';
import { errMsg } from '../util';

const ESPACE: Record<Role, string> = { ADMIN: '/admin', MEDECIN: '/medecin', PATIENT: '/patient' };

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="auth-box">
      <div class="tabs">
        <button type="button" [class.active]="mode() === 'login'" (click)="mode.set('login')">Connexion</button>
        <button type="button" [class.active]="mode() === 'register'" (click)="mode.set('register')">Inscription patient</button>
      </div>

      @if (mode() === 'login') {
        <form class="card single" (ngSubmit)="login()">
          <label>Login (email)
            <input name="email" type="email" [(ngModel)]="email" autocomplete="username" required />
          </label>
          <label>Mot de passe
            <input name="pwd" type="password" [(ngModel)]="motDePasse" autocomplete="current-password" required />
          </label>
          <button type="submit" [disabled]="loading()">Se connecter</button>
        </form>
      } @else {
        <form class="card single" (ngSubmit)="register()">
          <label>Nom <input name="nom" [(ngModel)]="reg.nom" required /></label>
          <label>Prénom <input name="prenom" [(ngModel)]="reg.prenom" required /></label>
          <label>Adresse <input name="adresse" [(ngModel)]="reg.adresse" /></label>
          <label>Email <input name="remail" type="email" [(ngModel)]="reg.email" autocomplete="username" required /></label>
          <label>Mot de passe (6 caractères minimum)
            <input name="rpwd" type="password" [(ngModel)]="reg.motDePasse" minlength="6" autocomplete="new-password" required />
          </label>
          <button type="submit" [disabled]="loading()">Créer mon compte</button>
        </form>
      }

      @if (error()) { <p class="error">{{ error() }}</p> }
      <p class="muted small">Les comptes médecins sont créés par l'administrateur du cabinet.</p>
    </div>
  `,
})
export class LoginComponent implements OnInit {
  private api = inject(ApiService);
  private auth = inject(AuthService);
  private router = inject(Router);

  mode = signal<'login' | 'register'>('login');
  loading = signal(false);
  error = signal('');

  email = '';
  motDePasse = '';
  reg = { nom: '', prenom: '', adresse: '', email: '', motDePasse: '' };

  ngOnInit(): void {
    // déjà connecté : direction son espace
    const role = this.auth.role();
    if (role) this.router.navigate([ESPACE[role]]);
  }

  login(): void {
    this.run(this.api.login(this.email, this.motDePasse));
  }

  register(): void {
    this.run(this.api.register(this.reg));
  }

  private run(call: import('rxjs').Observable<AuthResponse>): void {
    this.loading.set(true);
    this.error.set('');
    call.subscribe({
      next: res => {
        this.auth.start(res);
        this.router.navigate([ESPACE[res.role]]);
      },
      error: e => {
        this.loading.set(false);
        this.error.set(errMsg(e));
      },
    });
  }
}
