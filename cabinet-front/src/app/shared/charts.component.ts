import { Component, computed, input } from '@angular/core';

export interface Slice {
  label: string;
  value: number;
  color: string;
}

/** Anneau de répartition (SVG, sans bibliothèque). */
@Component({
  selector: 'app-donut',
  standalone: true,
  template: `
    @if (total() === 0) {
      <p class="muted">Aucune donnée.</p>
    } @else {
      <div class="donut-wrap">
        <svg viewBox="0 0 42 42" class="donut" role="img" aria-label="Répartition">
          <circle cx="21" cy="21" r="15.9155" fill="none" stroke="#e2e8f0" stroke-width="6" />
          @for (s of segments(); track s.label) {
            <circle cx="21" cy="21" r="15.9155" fill="none" stroke-width="6"
                    [attr.stroke]="s.color"
                    [attr.stroke-dasharray]="s.dash"
                    [attr.stroke-dashoffset]="s.offset" />
          }
          <text x="21" y="22.4" text-anchor="middle" class="donut-total">{{ total() }}</text>
        </svg>
        <ul class="legend">
          @for (s of data(); track s.label) {
            <li><span class="dot" [style.background]="s.color"></span>{{ s.label }} <b>{{ s.value }}</b></li>
          }
        </ul>
      </div>
    }
  `,
  styles: [`
    .donut-wrap { display: flex; align-items: center; gap: 1.5rem; flex-wrap: wrap; }
    .donut { width: 150px; height: 150px; }
    .donut-total { font-size: 7px; font-weight: 700; fill: #0f172a; }
    .legend { list-style: none; margin: 0; padding: 0; display: grid; gap: .4rem; font-size: .9rem; }
    .dot { display: inline-block; width: 10px; height: 10px; border-radius: 50%; margin-right: .5rem; }
  `],
})
export class DonutComponent {
  data = input.required<Slice[]>();
  total = computed(() => this.data().reduce((sum, s) => sum + s.value, 0));
  segments = computed(() => {
    const total = this.total();
    let cumulative = 0;
    return this.data()
      .filter(s => s.value > 0)
      .map(s => {
        const pct = (s.value / total) * 100;
        const seg = { label: s.label, color: s.color, dash: `${pct} ${100 - pct}`, offset: 25 - cumulative };
        cumulative += pct;
        return seg;
      });
  });
}

/** Barres horizontales : une ligne par libellé. */
@Component({
  selector: 'app-bars',
  standalone: true,
  template: `
    @if (rows().length === 0) {
      <p class="muted">Aucune donnée.</p>
    } @else {
      <div class="bars">
        @for (r of rows(); track r.label) {
          <div class="row">
            <span class="lbl" [title]="r.label">{{ r.label }}</span>
            <span class="track"><span class="fill" [style.width.%]="r.pct" [style.background]="color()"></span></span>
            <b class="val">{{ r.value }}</b>
          </div>
        }
      </div>
    }
  `,
  styles: [`
    .bars { display: grid; gap: .55rem; }
    .row { display: grid; grid-template-columns: minmax(90px, 38%) 1fr 2.2rem; gap: .6rem; align-items: center; font-size: .88rem; }
    .lbl { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: #334155; }
    .track { height: 10px; background: #e2e8f0; border-radius: 99px; overflow: hidden; }
    .fill { display: block; height: 100%; border-radius: 99px; min-width: 3px; }
    .val { text-align: right; }
  `],
})
export class BarsComponent {
  data = input.required<Record<string, number>>();
  color = input('#0f766e');
  rows = computed(() => {
    const entries = Object.entries(this.data());
    const max = Math.max(1, ...entries.map(([, v]) => v));
    return entries.map(([label, value]) => ({ label, value, pct: (value / max) * 100 }));
  });
}

/** Colonnes verticales (ex. rendez-vous par mois). */
@Component({
  selector: 'app-columns',
  standalone: true,
  template: `
    @if (cols().length === 0) {
      <p class="muted">Aucune donnée.</p>
    } @else {
      <div class="cols">
        @for (c of cols(); track c.label) {
          <div class="col">
            <b>{{ c.value }}</b>
            <span class="bar" [style.height.%]="c.pct" [style.background]="color()"></span>
            <span class="lbl">{{ c.label }}</span>
          </div>
        }
      </div>
    }
  `,
  styles: [`
    .cols { display: flex; align-items: flex-end; gap: .8rem; height: 190px; overflow-x: auto; padding-bottom: .2rem; }
    .col { flex: 1 0 44px; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: flex-end; gap: .3rem; font-size: .8rem; }
    .bar { width: 100%; max-width: 46px; border-radius: 6px 6px 0 0; min-height: 3px; }
    .lbl { color: #64748b; font-size: .72rem; white-space: nowrap; }
  `],
})
export class ColumnsComponent {
  data = input.required<Record<string, number>>();
  color = input('#0ea5e9');
  cols = computed(() => {
    const entries = Object.entries(this.data());
    const max = Math.max(1, ...entries.map(([, v]) => v));
    // la zone des barres occupe ~70 % de la hauteur, le reste sert aux libellés
    return entries.map(([label, value]) => ({ label, value, pct: (value / max) * 70 }));
  });
}
