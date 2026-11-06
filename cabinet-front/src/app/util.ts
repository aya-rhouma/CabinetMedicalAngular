import { HttpErrorResponse } from '@angular/common/http';
import { Rendezvous, StatutRdv } from './models';

/** Message lisible à partir d'une erreur HTTP du backend. */
export function errMsg(e: unknown): string {
  const err = e as HttpErrorResponse;
  if (err?.status === 0) return 'Serveur injoignable. Vérifiez que le backend est démarré.';
  const body = err?.error;
  if (body?.message) return body.message;
  if (body?.errors) return Object.values(body.errors).join(' · ');
  if (err?.status === 403) return 'Accès refusé.';
  return 'Une erreur est survenue.';
}

export const STATUT_LABEL: Record<StatutRdv, string> = {
  PROGRAMME: 'Programmé',
  PASSE: 'Passé',
  ANNULE: 'Annulé',
};

/** Les anciens rendez-vous (sans statut en base) sont considérés comme programmés. */
export const statutOf = (r: Rendezvous): StatutRdv => r.statut ?? 'PROGRAMME';

export const hhmm = (t: string | undefined | null): string => (t ?? '').slice(0, 5);

export const initials = (a: string, b: string): string =>
  ((a?.[0] ?? '') + (b?.[0] ?? '')).toUpperCase();

/** Date du jour au format yyyy-MM-dd (fuseau local). */
export function todayIso(): string {
  const d = new Date();
  return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 10);
}
