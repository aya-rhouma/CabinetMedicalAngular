import { Component, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { AuthService } from '../auth.service';
import { Specialite } from '../models';
import { errMsg } from '../util';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="profile-box">
      <h2>Mon profil</h2>

      <form class="card" (ngSubmit)="enregistrer()">
        <h3 class="full">Mes informations</h3>
        <label>Nom <input name="nom" [(ngModel)]="form.nom" required /></label>
        <label>Prénom <input name="prenom" [(ngModel)]="form.prenom" required /></label>
        @if (role() !== 'ADMIN') {
          <label>Adresse <input name="adresse" [(ngModel)]="form.adresse" /></label>
        }
        <label>Login (email)
          <input name="email" type="email" [(ngModel)]="form.email" required />
        </label>
        @if (role() === 'MEDECIN') {
          <label>Spécialité
            <select name="spec" [(ngModel)]="form.idspec" required>
              <option [ngValue]="null" disabled>Choisir…</option>
              @for (s of specialites(); track s.idspec) { <option [ngValue]="s.idspec">{{ s.nomspecialite }}</option> }
            </select>
          </label>
        }
        <button type="submit" [disabled]="chargement()">Enregistrer</button>
        @if (errProfil()) { <p class="error full">{{ errProfil() }}</p> }
        @if (okProfil()) { <p class="ok full">{{ okProfil() }}</p> }
      </form>

      <form class="card" (ngSubmit)="changerMotDePasse()">
        <h3 class="full">Mot de passe</h3>
        <label>Ancien mot de passe
          <input name="old" type="password" [(ngModel)]="pwd.ancien" autocomplete="current-password" required />
        </label>
        <label>Nouveau mot de passe (6 caractères minimum)
          <input name="new" type="password" [(ngModel)]="pwd.nouveau" minlength="6" autocomplete="new-password" required />
        </label>
        <label>Confirmer le nouveau mot de passe
          <input name="confirm" type="password" [(ngModel)]="pwd.confirmation" autocomplete="new-password" required />
        </label>
        <button type="submit">Changer le mot de passe</button>
        @if (errPwd()) { <p class="error full">{{ errPwd() }}</p> }
        @if (okPwd()) { <p class="ok full">{{ okPwd() }}</p> }
      </form>

      @if (role() === 'PATIENT') {
        <form class="card danger-zone" (ngSubmit)="supprimerCompte()">
          <h3 class="full">Supprimer mon compte</h3>
          <p class="muted small full">
            Cette action est définitive : votre profil, vos rendez-vous et vos consultations seront supprimés.
            Saisissez votre mot de passe pour confirmer.
          </p>
          <label>Mot de passe
            <input name="delpwd" type="password" [(ngModel)]="mdpSuppression" autocomplete="current-password" required />
          </label>
          <button type="submit" class="danger">Supprimer mon compte</button>
          @if (errSuppr()) { <p class="error full">{{ errSuppr() }}</p> }
        </form>
      }
    </div>
  `,
})
export class ProfileComponent implements OnInit {
  private api = inject(ApiService);
  private auth = inject(AuthService);
  private router = inject(Router);

  readonly role = this.auth.role;
  specialites = signal<Specialite[]>([]);
  chargement = signal(true);

  form = { nom: '', prenom: '', adresse: '', email: '', idspec: null as number | null };
  pwd = { ancien: '', nouveau: '', confirmation: '' };
  mdpSuppression = '';

  errProfil = signal('');
  okProfil = signal('');
  errPwd = signal('');
  okPwd = signal('');
  errSuppr = signal('');

  ngOnInit(): void {
    this.api.getProfile().subscribe({
      next: p => {
        this.form = {
          nom: p.nom, prenom: p.prenom, adresse: p.adresse ?? '', email: p.email, idspec: p.idspec,
        };
        this.chargement.set(false);
      },
      error: e => { this.errProfil.set(errMsg(e)); this.chargement.set(false); },
    });
    if (this.role() === 'MEDECIN') {
      this.api.publicSpecialites().subscribe({ next: r => this.specialites.set(r) });
    }
  }

  enregistrer(): void {
    this.errProfil.set('');
    this.okProfil.set('');
    this.api.updateProfile(this.form).subscribe({
      next: session => {
        // le login (email) fait partie du jeton : on remplace la session
        this.auth.start(session);
        this.okProfil.set('Profil mis à jour.');
      },
      error: e => this.errProfil.set(errMsg(e)),
    });
  }

  changerMotDePasse(): void {
    this.errPwd.set('');
    this.okPwd.set('');
    if (this.pwd.nouveau !== this.pwd.confirmation) {
      this.errPwd.set('La confirmation ne correspond pas au nouveau mot de passe.');
      return;
    }
    this.api.changePassword(this.pwd.ancien, this.pwd.nouveau).subscribe({
      next: r => {
        this.okPwd.set(r.message);
        this.pwd = { ancien: '', nouveau: '', confirmation: '' };
      },
      error: e => this.errPwd.set(errMsg(e)),
    });
  }

  supprimerCompte(): void {
    this.errSuppr.set('');
    if (!confirm('Supprimer définitivement votre compte, vos rendez-vous et vos consultations ?')) return;
    this.api.supprimerCompte(this.mdpSuppression).subscribe({
      next: () => {
        this.auth.logout();
        this.router.navigate(['/']);
      },
      error: e => this.errSuppr.set(errMsg(e)),
    });
  }
}
