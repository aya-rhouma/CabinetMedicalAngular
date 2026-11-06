import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './auth.service';
import { Role } from './models';

const ESPACE: Record<Role, string> = {
  ADMIN: '/admin',
  MEDECIN: '/medecin',
  PATIENT: '/patient',
};

const ROLE_LABEL: Record<Role, string> = {
  ADMIN: 'Admin',
  MEDECIN: 'Médecin',
  PATIENT: 'Patient',
};

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <header class="top">
      <div class="top-inner">
        <a class="brand" routerLink="/">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <rect x="1" y="1" width="22" height="22" rx="6" fill="#fff" opacity=".18" />
            <path d="M10 5h4v5h5v4h-5v5h-4v-5H5v-4h5z" fill="#fff" />
          </svg>
          <span>Cabinet médical</span>
        </a>
        <nav>
          <a routerLink="/" routerLinkActive="on" [routerLinkActiveOptions]="{ exact: true }">Accueil</a>
          @if (auth.session(); as s) {
            <a [routerLink]="espace()" routerLinkActive="on">Mon espace</a>
            <a routerLink="/profil" routerLinkActive="on">Profil</a>
            <span class="who" [title]="s.email">{{ s.nomComplet }} · {{ roleLabel() }}</span>
            <button type="button" class="logout" (click)="logout()">Déconnexion</button>
          } @else {
            <a routerLink="/login" routerLinkActive="on">Connexion</a>
          }
        </nav>
      </div>
    </header>
    <main><router-outlet /></main>
  `,
  styles: [`
    .top {
      background: linear-gradient(135deg, #0f766e, #0d9488);
      color: #fff;
      box-shadow: 0 2px 12px rgba(15, 118, 110, .35);
      position: sticky;
      top: 0;
      z-index: 10;
    }
    .top-inner {
      max-width: 1100px;
      margin: 0 auto;
      padding: .8rem 1.25rem;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 1.5rem;
      flex-wrap: wrap;
    }
    .brand {
      display: flex; align-items: center; gap: .6rem;
      font-size: 1.15rem; font-weight: 700; letter-spacing: -.01em;
      color: #fff; text-decoration: none;
    }
    nav { display: flex; gap: .3rem; flex-wrap: wrap; align-items: center; }
    nav a {
      color: #ccfbf1; text-decoration: none; font-weight: 500; font-size: .93rem;
      padding: .45rem .9rem; border-radius: 999px; transition: background .15s, color .15s;
    }
    nav a:hover { background: rgba(255, 255, 255, .15); color: #fff; }
    nav a.on { background: #fff; color: #0f766e; font-weight: 600; }
    .who { font-size: .85rem; color: #ccfbf1; margin: 0 .4rem 0 .8rem; }
    .logout {
      background: rgba(255, 255, 255, .15); color: #fff; padding: .4rem .9rem; font-size: .88rem;
    }
    .logout:hover { background: rgba(255, 255, 255, .28); }
    main { max-width: 1100px; margin: 1.75rem auto; padding: 0 1.25rem 3rem; }
  `],
})
export class AppComponent {
  readonly auth = inject(AuthService);
  private router = inject(Router);

  readonly espace = computed(() => ESPACE[this.auth.role() ?? 'PATIENT']);
  readonly roleLabel = computed(() => ROLE_LABEL[this.auth.role() ?? 'PATIENT']);

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
