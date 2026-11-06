import { Injectable, computed, signal } from '@angular/core';
import { AuthResponse } from './models';

const KEY = 'cabinet_session';

function load(): AuthResponse | null {
  try {
    const raw = localStorage.getItem(KEY);
    return raw ? (JSON.parse(raw) as AuthResponse) : null;
  } catch {
    return null;
  }
}

/** Session de l'utilisateur connecté (jeton JWT conservé dans le navigateur). */
@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly session = signal<AuthResponse | null>(load());
  readonly role = computed(() => this.session()?.role ?? null);
  readonly isLoggedIn = computed(() => this.session() !== null);

  get token(): string | null {
    return this.session()?.token ?? null;
  }

  start(res: AuthResponse): void {
    this.session.set(res);
    try { localStorage.setItem(KEY, JSON.stringify(res)); } catch { /* stockage indisponible */ }
  }

  logout(): void {
    this.session.set(null);
    try { localStorage.removeItem(KEY); } catch { /* stockage indisponible */ }
  }
}
