import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { Medecin, Rendezvous, StatutRdv } from '../models';
import { errMsg, hhmm, statutOf, todayIso } from '../util';
import { DetailComponent, StatutBadgeComponent } from '../shared/ui.components';

type Filtre = 'TOUS' | StatutRdv;

@Component({
  selector: 'app-patient-space',
  standalone: true,
  imports: [FormsModule, StatutBadgeComponent, DetailComponent],
  template: `
    <h2>Mon espace patient</h2>

    <form class="card" (ngSubmit)="reserver()">
      <label>Médecin
        <select name="medecin" [(ngModel)]="idMedecin" required>
          <option [ngValue]="null" disabled>Choisir un médecin…</option>
          @for (m of medecins(); track m.id) {
            <option [ngValue]="m.id">Dr {{ m.prenom }} {{ m.nom }} · {{ m.specialite.nomspecialite }}</option>
          }
        </select>
      </label>
      <label>Date <input name="date" type="date" [min]="today" [(ngModel)]="date" required /></label>
      <label>Heure <input name="heure" type="time" [(ngModel)]="heureSaisie" required /></label>
      <button type="submit">Prendre rendez-vous</button>
    </form>

    @if (error()) { <p class="error">{{ error() }}</p> }
    @if (ok()) { <p class="ok">{{ ok() }}</p> }

    <div class="toolbar">
      <select aria-label="Filtrer par médecin" [ngModel]="medecinFiltre()" (ngModelChange)="medecinFiltre.set($event)">
        <option [ngValue]="null">Tous les médecins</option>
        @for (m of medecinsRdv(); track m.id) {
          <option [ngValue]="m.id">Dr {{ m.prenom }} {{ m.nom }} · {{ m.specialite.nomspecialite }}</option>
        }
      </select>
      @for (f of filtres; track f.valeur) {
        <button type="button" class="pill" [class.active]="filtre() === f.valeur" (click)="filtre.set(f.valeur)">
          {{ f.label }} <b>{{ compte()[f.valeur] }}</b>
        </button>
      }
    </div>

    <div class="table-wrap">
      <table>
        <tr>
          <th>Date</th><th>Heure</th><th>Médecin</th><th>Statut</th>
          <th>Récapitulatif</th><th>Ordonnance</th><th>Certificat</th><th></th>
        </tr>
        @for (r of affiches(); track r.idrdv) {
          <tr>
            <td>{{ r.daterdv }}</td>
            <td>{{ hh(r.heurerdv) }}</td>
            <td>Dr {{ r.medecin.prenom }} {{ r.medecin.nom }}<br /><small class="muted">{{ r.medecin.specialite.nomspecialite }}</small></td>
            <td><app-statut [statut]="statut(r)" /></td>
            <td><app-detail [texte]="r.consultation?.recapcons" titre="Lire" /></td>
            <td><app-detail [texte]="r.consultation?.ordonnance" titre="Voir l'ordonnance" /></td>
            <td><app-detail [texte]="r.consultation?.certificat" titre="Voir le certificat" /></td>
            <td>
              @if (statut(r) === 'PROGRAMME') {
                <button type="button" class="danger" (click)="annuler(r)">Annuler</button>
              }
            </td>
          </tr>
        } @empty {
          <tr>
            <td colspan="8" class="muted">
              {{ rdvs().length === 0 ? "Vous n'avez encore aucun rendez-vous." : 'Aucun rendez-vous pour ce filtre.' }}
            </td>
          </tr>
        }
      </table>
    </div>
  `,
})
export class PatientSpaceComponent implements OnInit {
  private api = inject(ApiService);

  medecins = signal<Medecin[]>([]);
  rdvs = signal<Rendezvous[]>([]);
  error = signal('');
  ok = signal('');

  // filtres d'affichage
  filtre = signal<Filtre>('TOUS');
  medecinFiltre = signal<number | null>(null);

  // formulaire de réservation
  readonly today = todayIso();
  idMedecin: number | null = null;
  date = '';
  heureSaisie = '';

  readonly statut = statutOf;
  readonly hh = hhmm;
  readonly filtres: { valeur: Filtre; label: string }[] = [
    { valeur: 'TOUS', label: 'Tous' },
    { valeur: 'PROGRAMME', label: 'Programmés' },
    { valeur: 'PASSE', label: 'Passés' },
    { valeur: 'ANNULE', label: 'Annulés' },
  ];

  /** Médecins avec lesquels le patient a (ou a eu) un rendez-vous. */
  medecinsRdv = computed(() => {
    const parId = new Map<number, Medecin>();
    this.rdvs().forEach(r => parId.set(r.medecin.id, r.medecin));
    return [...parId.values()].sort((a, b) =>
      `${a.nom} ${a.prenom}`.localeCompare(`${b.nom} ${b.prenom}`));
  });

  /** Rendez-vous du médecin sélectionné (ou tous). */
  private parMedecin = computed(() => {
    const id = this.medecinFiltre();
    return id === null ? this.rdvs() : this.rdvs().filter(r => r.medecin.id === id);
  });

  compte = computed<Record<Filtre, number>>(() => {
    const c: Record<Filtre, number> = { TOUS: 0, PROGRAMME: 0, PASSE: 0, ANNULE: 0 };
    this.parMedecin().forEach(r => { c.TOUS++; c[statutOf(r)]++; });
    return c;
  });

  affiches = computed(() => {
    const f = this.filtre();
    const base = this.parMedecin();
    return f === 'TOUS' ? base : base.filter(r => statutOf(r) === f);
  });

  ngOnInit(): void {
    this.api.publicMedecins().subscribe({
      next: r => this.medecins.set(r),
      error: e => this.error.set(errMsg(e)),
    });
    this.load();
  }

  load(): void {
    this.api.patientRdv().subscribe({
      next: r => this.rdvs.set(r),
      error: e => this.error.set(errMsg(e)),
    });
  }

  reserver(): void {
    this.error.set('');
    this.ok.set('');
    if (this.idMedecin === null) return;
    const heurerdv = this.heureSaisie.length === 5 ? this.heureSaisie + ':00' : this.heureSaisie;
    this.api.prendreRdv({ idMedecin: this.idMedecin, daterdv: this.date, heurerdv }).subscribe({
      next: () => {
        this.ok.set('Rendez-vous enregistré.');
        this.idMedecin = null;
        this.date = '';
        this.heureSaisie = '';
        this.load();
      },
      error: e => this.error.set(errMsg(e)),
    });
  }

  annuler(r: Rendezvous): void {
    if (!confirm('Annuler ce rendez-vous ?')) return;
    this.error.set('');
    this.ok.set('');
    this.api.annulerRdv(r.idrdv).subscribe({
      next: () => { this.ok.set('Rendez-vous annulé.'); this.load(); },
      error: e => this.error.set(errMsg(e)),
    });
  }
}
