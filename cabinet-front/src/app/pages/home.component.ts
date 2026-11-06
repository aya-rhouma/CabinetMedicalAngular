import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { AuthService } from '../auth.service';
import { Medecin } from '../models';
import { errMsg, initials } from '../util';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <section class="hero">
      <h2>Prenez soin de votre santé</h2>
      <p>Retrouvez nos médecins, leur spécialité et leurs coordonnées. Connectez-vous pour prendre rendez-vous en quelques clics.</p>
      @if (!auth.isLoggedIn()) {
        <a class="cta" routerLink="/login">Se connecter / Créer un compte</a>
      }
    </section>

    <div class="toolbar">
      <input type="search" placeholder="Rechercher un médecin…" [ngModel]="recherche()" (ngModelChange)="recherche.set($event)" />
      <select [ngModel]="specialite()" (ngModelChange)="specialite.set($event)">
        <option value="">Toutes les spécialités</option>
        @for (s of specialites(); track s) { <option [value]="s">{{ s }}</option> }
      </select>
    </div>

    @if (error()) { <p class="error">{{ error() }}</p> }

    <div class="grid-cards">
      @for (m of filtres(); track m.id) {
        <article class="doc-card">
          <div class="avatar">{{ initiales(m) }}</div>
          <h3>Dr {{ m.prenom }} {{ m.nom }}</h3>
          <span class="chip">{{ m.specialite.nomspecialite }}</span>
          <ul class="infos">
            <li><span aria-hidden="true">📍</span> {{ m.adr || 'Adresse non renseignée' }}</li>
            <li><span aria-hidden="true">✉️</span> @if (m.email) { <a [href]="'mailto:' + m.email">{{ m.email }}</a> } @else { —  }</li>
          </ul>
        </article>
      } @empty {
        @if (!error()) { <p class="muted">Aucun médecin trouvé.</p> }
      }
    </div>
  `,
})
export class HomeComponent implements OnInit {
  private api = inject(ApiService);
  readonly auth = inject(AuthService);

  medecins = signal<Medecin[]>([]);
  error = signal('');
  recherche = signal('');
  specialite = signal('');

  specialites = computed(() =>
    [...new Set(this.medecins().map(m => m.specialite.nomspecialite))].sort());

  filtres = computed(() => {
    const q = this.recherche().trim().toLowerCase();
    const spec = this.specialite();
    return this.medecins().filter(m =>
      (!spec || m.specialite.nomspecialite === spec) &&
      (!q || `${m.prenom} ${m.nom} ${m.specialite.nomspecialite}`.toLowerCase().includes(q)));
  });

  ngOnInit(): void {
    this.api.publicMedecins().subscribe({
      next: r => this.medecins.set(r),
      error: e => this.error.set(errMsg(e)),
    });
  }

  initiales(m: Medecin): string {
    return initials(m.prenom, m.nom);
  }
}
