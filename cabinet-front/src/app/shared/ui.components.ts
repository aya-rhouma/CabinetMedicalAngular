import { Component, computed, input } from '@angular/core';
import { StatutRdv } from '../models';
import { STATUT_LABEL } from '../util';

/** Pastille de statut : Programmé / Passé / Annulé. */
@Component({
  selector: 'app-statut',
  standalone: true,
  template: `
    <span class="badge"
          [class.programme]="statut() === 'PROGRAMME'"
          [class.passe]="statut() === 'PASSE'"
          [class.annule]="statut() === 'ANNULE'">{{ label() }}</span>
  `,
})
export class StatutBadgeComponent {
  statut = input.required<StatutRdv>();
  label = computed(() => STATUT_LABEL[this.statut()]);
}

/** Texte long (récapitulatif, ordonnance, certificat) replié dans la cellule du tableau. */
@Component({
  selector: 'app-detail',
  standalone: true,
  template: `
    @if (texte()) {
      <details class="detail">
        <summary>{{ titre() }}</summary>
        <p class="pre">{{ texte() }}</p>
      </details>
    } @else {
      <span class="muted">—</span>
    }
  `,
})
export class DetailComponent {
  texte = input<string | null | undefined>('');
  titre = input('Voir');
}
