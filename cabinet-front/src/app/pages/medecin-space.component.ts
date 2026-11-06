import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { Rendezvous, StatutRdv } from '../models';
import { errMsg, hhmm, statutOf, todayIso } from '../util';
import { DetailComponent, StatutBadgeComponent } from '../shared/ui.components';

type Filtre = 'TOUS' | StatutRdv;

@Component({
  selector: 'app-medecin-space',
  standalone: true,
  imports: [FormsModule, StatutBadgeComponent, DetailComponent],
  template: `
    <h2>Mon espace médecin</h2>

    @if (error()) { <p class="error">{{ error() }}</p> }
    @if (ok()) { <p class="ok">{{ ok() }}</p> }

    @if (selection(); as r) {
      <form class="card panel" (ngSubmit)="enregistrer()">
        <h3 class="full">
          {{ r.consultation ? 'Modifier la consultation' : 'Nouvelle consultation' }} —
          {{ r.patient.prenompatient }} {{ r.patient.nompatient }}, {{ r.daterdv }} à {{ hh(r.heurerdv) }}
        </h3>
        <label>Date de la consultation
          <input name="datecons" type="date" [(ngModel)]="form.datecons" required />
        </label>
        <label class="full">Récapitulatif
          <textarea name="recap" rows="3" [(ngModel)]="form.recapcons" placeholder="Résumé de la consultation"></textarea>
        </label>
        <label class="full">Ordonnance <span class="muted">(facultatif)</span>
          <textarea name="ordo" rows="4" [(ngModel)]="form.ordonnance" placeholder="Médicaments, posologie, durée…"></textarea>
        </label>
        <label class="full">Certificat médical <span class="muted">(facultatif)</span>
          <textarea name="certif" rows="3" [(ngModel)]="form.certificat" placeholder="Texte du certificat"></textarea>
        </label>
        <div class="actions full">
          <button type="submit">Enregistrer</button>
          <button type="button" class="ghost" (click)="selection.set(null)">Fermer</button>
        </div>
      </form>
    }

    <div class="toolbar">
      @for (f of filtres; track f.valeur) {
        <button type="button" class="pill" [class.active]="filtre() === f.valeur" (click)="filtre.set(f.valeur)">
          {{ f.label }} <b>{{ compte()[f.valeur] }}</b>
        </button>
      }
    </div>

    <div class="table-wrap">
      <table>
        <tr>
          <th>Date</th><th>Heure</th><th>Patient</th><th>Statut</th>
          <th>Récapitulatif</th><th>Ordonnance</th><th>Certificat</th><th></th>
        </tr>
        @for (r of affiches(); track r.idrdv) {
          <tr>
            <td>{{ r.daterdv }}</td>
            <td>{{ hh(r.heurerdv) }}</td>
            <td>{{ r.patient.prenompatient }} {{ r.patient.nompatient }}</td>
            <td><app-statut [statut]="statut(r)" /></td>
            <td><app-detail [texte]="r.consultation?.recapcons" titre="Lire" /></td>
            <td><app-detail [texte]="r.consultation?.ordonnance" titre="Voir l'ordonnance" /></td>
            <td><app-detail [texte]="r.consultation?.certificat" titre="Voir le certificat" /></td>
            <td>
              @if (statut(r) === 'PROGRAMME') {
                <button type="button" (click)="changer(r, 'PASSE')">Marquer passé</button>
                <button type="button" class="danger" (click)="changer(r, 'ANNULE')">Annuler</button>
              }
              @if (statut(r) !== 'ANNULE') {
                <button type="button" (click)="ouvrir(r)">{{ r.consultation ? 'Modifier consultation' : 'Consultation' }}</button>
              }
            </td>
          </tr>
        } @empty {
          <tr><td colspan="8" class="muted">Aucun rendez-vous.</td></tr>
        }
      </table>
    </div>
  `,
})
export class MedecinSpaceComponent implements OnInit {
  private api = inject(ApiService);

  rdvs = signal<Rendezvous[]>([]);
  selection = signal<Rendezvous | null>(null);
  filtre = signal<Filtre>('TOUS');
  error = signal('');
  ok = signal('');

  form = { datecons: '', recapcons: '', ordonnance: '', certificat: '' };

  readonly statut = statutOf;
  readonly hh = hhmm;
  readonly filtres: { valeur: Filtre; label: string }[] = [
    { valeur: 'TOUS', label: 'Tous' },
    { valeur: 'PROGRAMME', label: 'Programmés' },
    { valeur: 'PASSE', label: 'Passés' },
    { valeur: 'ANNULE', label: 'Annulés' },
  ];

  compte = computed<Record<Filtre, number>>(() => {
    const c: Record<Filtre, number> = { TOUS: 0, PROGRAMME: 0, PASSE: 0, ANNULE: 0 };
    this.rdvs().forEach(r => { c.TOUS++; c[statutOf(r)]++; });
    return c;
  });

  affiches = computed(() => {
    const f = this.filtre();
    return f === 'TOUS' ? this.rdvs() : this.rdvs().filter(r => statutOf(r) === f);
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.api.medecinRdv().subscribe({
      next: r => this.rdvs.set(r),
      error: e => this.error.set(errMsg(e)),
    });
  }

  changer(r: Rendezvous, statut: StatutRdv): void {
    if (statut === 'ANNULE' && !confirm('Annuler ce rendez-vous ?')) return;
    this.error.set('');
    this.ok.set('');
    this.api.changerStatut(r.idrdv, statut).subscribe({
      next: () => { this.ok.set('Statut mis à jour.'); this.load(); },
      error: e => this.error.set(errMsg(e)),
    });
  }

  ouvrir(r: Rendezvous): void {
    this.error.set('');
    this.ok.set('');
    this.form = {
      datecons: r.consultation?.datecons ?? todayIso(),
      recapcons: r.consultation?.recapcons ?? '',
      ordonnance: r.consultation?.ordonnance ?? '',
      certificat: r.consultation?.certificat ?? '',
    };
    this.selection.set(r);
  }

  enregistrer(): void {
    const r = this.selection();
    if (!r) return;
    const call = r.consultation
      ? this.api.modifierConsultation(r.idrdv, this.form)
      : this.api.ajouterConsultation(r.idrdv, this.form);
    call.subscribe({
      next: () => {
        this.ok.set('Consultation enregistrée.');
        this.selection.set(null);
        this.load();
      },
      error: e => this.error.set(errMsg(e)),
    });
  }
}
