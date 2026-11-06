import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { Medecin, Specialite, Stats } from '../models';
import { errMsg, STATUT_LABEL } from '../util';
import { BarsComponent, ColumnsComponent, DonutComponent, Slice } from '../shared/charts.component';

const COULEURS: Record<string, string> = {
  PROGRAMME: '#0ea5e9',
  PASSE: '#16a34a',
  ANNULE: '#dc2626',
};

@Component({
  selector: 'app-admin-space',
  standalone: true,
  imports: [FormsModule, DonutComponent, BarsComponent, ColumnsComponent],
  template: `
    <h2>Tableau de bord administrateur</h2>

    @if (stats(); as s) {
      <div class="stats">
        <div class="stat"><b>{{ s.totaux['medecins'] }}</b><span>Médecins</span></div>
        <div class="stat"><b>{{ s.totaux['patients'] }}</b><span>Patients</span></div>
        <div class="stat"><b>{{ s.totaux['rendezvous'] }}</b><span>Rendez-vous</span></div>
        <div class="stat"><b>{{ s.totaux['consultations'] }}</b><span>Consultations</span></div>
      </div>

      <div class="chart-grid">
        <section class="panel">
          <h3>Rendez-vous par statut</h3>
          <app-donut [data]="parStatut()" />
        </section>
        <section class="panel">
          <h3>Rendez-vous par mois</h3>
          <app-columns [data]="s.parMois" />
        </section>
        <section class="panel">
          <h3>Médecins par spécialité</h3>
          <app-bars [data]="s.parSpecialite" color="#0f766e" />
        </section>
        <section class="panel">
          <h3>Médecins les plus sollicités</h3>
          <app-bars [data]="s.parMedecin" color="#6366f1" />
        </section>
      </div>
    }

    @if (error()) { <p class="error">{{ error() }}</p> }
    @if (ok()) { <p class="ok">{{ ok() }}</p> }

    <!-- ===================== Spécialités ===================== -->
    <h2 class="section" id="form-specialite">Spécialités</h2>
    <form class="card inline" (ngSubmit)="enregistrerSpecialite()">
      <label>{{ editSpecId() === null ? 'Nouvelle spécialité' : 'Modifier la spécialité' }}
        <input name="nomspec" [(ngModel)]="nomSpecialite" placeholder="Ex. Cardiologie" required />
      </label>
      <div class="actions full">
        <button type="submit">{{ editSpecId() === null ? 'Ajouter la spécialité' : 'Enregistrer les modifications' }}</button>
        @if (editSpecId() !== null) {
          <button type="button" class="ghost" (click)="annulerSpecialite()">Annuler</button>
        }
      </div>
    </form>

    <div class="table-wrap">
      <table>
        <tr><th>Spécialité</th><th>Médecins</th><th></th></tr>
        @for (sp of specialites(); track sp.idspec) {
          <tr [class.editing]="editSpecId() === sp.idspec">
            <td>{{ sp.nomspecialite }}</td>
            <td>{{ nbMedecins(sp) }}</td>
            <td>
              <button type="button" (click)="modifierSpecialite(sp)">Modifier</button>
              <button type="button" class="danger" [disabled]="nbMedecins(sp) > 0"
                      [title]="nbMedecins(sp) > 0 ? 'Utilisée par ' + nbMedecins(sp) + ' médecin(s)' : 'Supprimer'"
                      (click)="supprimerSpecialite(sp)">Supprimer</button>
            </td>
          </tr>
        } @empty {
          <tr><td colspan="3" class="muted">Aucune spécialité. Ajoutez-en une avant de créer un médecin.</td></tr>
        }
      </table>
    </div>

    <!-- ===================== Médecins ===================== -->
    <h2 class="section" id="form-medecin">{{ editId() === null ? 'Ajouter un médecin' : 'Modifier le médecin' }}</h2>
    <form class="card" (ngSubmit)="enregistrerMedecin()">
      <label>Nom <input name="nom" [(ngModel)]="med.nom" required /></label>
      <label>Prénom <input name="prenom" [(ngModel)]="med.prenom" required /></label>
      <label>Adresse <input name="adr" [(ngModel)]="med.adr" /></label>
      <label>Email (identifiant de connexion)
        <input name="email" type="email" [(ngModel)]="med.email" autocomplete="off" required />
      </label>
      @if (editId() === null) {
        <label>Mot de passe initial
          <input name="pwd" type="password" [(ngModel)]="med.motDePasse" minlength="6" autocomplete="new-password" required />
        </label>
      }
      <label>Spécialité
        <select name="spec" [(ngModel)]="med.idspec" required>
          <option [ngValue]="null" disabled>Choisir…</option>
          @for (sp of specialites(); track sp.idspec) { <option [ngValue]="sp.idspec">{{ sp.nomspecialite }}</option> }
        </select>
      </label>
      <div class="actions full">
        <button type="submit">{{ editId() === null ? 'Créer le médecin' : 'Enregistrer les modifications' }}</button>
        @if (editId() !== null) {
          <button type="button" class="ghost" (click)="annulerEdition()">Annuler</button>
        }
      </div>
    </form>

    <div class="table-wrap">
      <table>
        <tr><th>Nom</th><th>Spécialité</th><th>Adresse</th><th>Email</th><th></th></tr>
        @for (m of medecins(); track m.id) {
          <tr [class.editing]="editId() === m.id">
            <td>Dr {{ m.prenom }} {{ m.nom }}</td>
            <td>{{ m.specialite.nomspecialite }}</td>
            <td>{{ m.adr }}</td>
            <td>{{ m.email }}</td>
            <td>
              <button type="button" (click)="modifier(m)">Modifier</button>
              <button type="button" class="danger" (click)="supprimer(m)">Supprimer</button>
            </td>
          </tr>
        } @empty {
          <tr><td colspan="5" class="muted">Aucun médecin.</td></tr>
        }
      </table>
    </div>
  `,
})
export class AdminSpaceComponent implements OnInit {
  private api = inject(ApiService);

  stats = signal<Stats | null>(null);
  medecins = signal<Medecin[]>([]);
  specialites = signal<Specialite[]>([]);
  error = signal('');
  ok = signal('');

  /** id du médecin en cours de modification (null = création). */
  editId = signal<number | null>(null);
  med = AdminSpaceComponent.vide();

  /** id de la spécialité en cours de modification (null = création). */
  editSpecId = signal<number | null>(null);
  nomSpecialite = '';

  parStatut = computed<Slice[]>(() => {
    const s = this.stats();
    if (!s) return [];
    return Object.entries(s.parStatut).map(([cle, value]) => ({
      label: STATUT_LABEL[cle as keyof typeof STATUT_LABEL] ?? cle,
      value,
      color: COULEURS[cle] ?? '#94a3b8',
    }));
  });

  private static vide() {
    return { nom: '', prenom: '', adr: '', email: '', motDePasse: '', idspec: null as number | null };
  }

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.api.stats().subscribe({
      next: r => this.stats.set(r),
      error: e => this.error.set(errMsg(e)),
    });
    this.api.adminMedecins().subscribe({
      next: r => this.medecins.set(r),
      error: e => this.error.set(errMsg(e)),
    });
    this.api.adminSpecialites().subscribe({
      next: r => this.specialites.set(r),
      error: e => this.error.set(errMsg(e)),
    });
  }

  private reset(): void {
    this.error.set('');
    this.ok.set('');
  }

  private allerA(id: string): void {
    setTimeout(() => document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' }));
  }

  // ---------- Spécialités ----------

  nbMedecins(sp: Specialite): number {
    return this.medecins().filter(m => m.specialite.idspec === sp.idspec).length;
  }

  modifierSpecialite(sp: Specialite): void {
    this.reset();
    this.nomSpecialite = sp.nomspecialite;
    this.editSpecId.set(sp.idspec);
    this.allerA('form-specialite');
  }

  annulerSpecialite(): void {
    this.editSpecId.set(null);
    this.nomSpecialite = '';
  }

  enregistrerSpecialite(): void {
    this.reset();
    const nom = this.nomSpecialite.trim();
    if (!nom) return;
    const id = this.editSpecId();
    const call = id === null
      ? this.api.creerSpecialite(nom)
      : this.api.modifierSpecialite(id, nom);
    call.subscribe({
      next: s => {
        this.ok.set(id === null
          ? `Spécialité « ${s.nomspecialite} » ajoutée.`
          : `Spécialité « ${s.nomspecialite} » modifiée.`);
        this.annulerSpecialite();
        this.loadAll();
      },
      error: e => this.error.set(errMsg(e)),
    });
  }

  supprimerSpecialite(sp: Specialite): void {
    if (!confirm(`Supprimer la spécialité « ${sp.nomspecialite} » ?`)) return;
    this.reset();
    this.api.supprimerSpecialite(sp.idspec).subscribe({
      next: () => {
        this.ok.set(`Spécialité « ${sp.nomspecialite} » supprimée.`);
        if (this.editSpecId() === sp.idspec) this.annulerSpecialite();
        this.loadAll();
      },
      error: e => this.error.set(errMsg(e)),
    });
  }

  // ---------- Médecins ----------

  /** Charge le médecin dans le formulaire (mot de passe non modifiable ici). */
  modifier(m: Medecin): void {
    this.reset();
    this.med = {
      nom: m.nom, prenom: m.prenom, adr: m.adr ?? '', email: m.email ?? '',
      motDePasse: '', idspec: m.specialite.idspec,
    };
    this.editId.set(m.id);
    this.allerA('form-medecin');
  }

  annulerEdition(): void {
    this.editId.set(null);
    this.med = AdminSpaceComponent.vide();
  }

  enregistrerMedecin(): void {
    this.reset();
    if (this.med.idspec === null) return;
    const idspec = this.med.idspec;
    const id = this.editId();

    if (id === null) {
      this.api.creerMedecin({ ...this.med, idspec }).subscribe({
        next: m => {
          this.ok.set(`Dr ${m.prenom} ${m.nom} a été ajouté. Il peut se connecter avec son email.`);
          this.med = AdminSpaceComponent.vide();
          this.loadAll();
        },
        error: e => this.error.set(errMsg(e)),
      });
    } else {
      this.api.modifierMedecin(id, {
        nom: this.med.nom, prenom: this.med.prenom, adr: this.med.adr, email: this.med.email, idspec,
      }).subscribe({
        next: m => {
          this.ok.set(`Dr ${m.prenom} ${m.nom} a été modifié.`);
          this.annulerEdition();
          this.loadAll();
        },
        error: e => this.error.set(errMsg(e)),
      });
    }
  }

  supprimer(m: Medecin): void {
    const ok = confirm(
      `Supprimer Dr ${m.prenom} ${m.nom} ?\n\nSon compte, ses rendez-vous et leurs consultations seront aussi supprimés. Cette action est définitive.`);
    if (!ok) return;
    this.reset();
    this.api.supprimerMedecin(m.id).subscribe({
      next: () => {
        this.ok.set(`Dr ${m.prenom} ${m.nom} a été supprimé.`);
        if (this.editId() === m.id) this.annulerEdition();
        this.loadAll();
      },
      error: e => this.error.set(errMsg(e)),
    });
  }
}
