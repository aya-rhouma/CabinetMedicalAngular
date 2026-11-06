import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Role } from './models';

/** Autorise uniquement le rôle indiqué, sinon redirige vers la connexion. */
export const roleGuard = (role: Role): CanActivateFn => () => {
  const auth = inject(AuthService);
  return auth.role() === role ? true : inject(Router).createUrlTree(['/login']);
};

/** Autorise tout utilisateur connecté. */
export const loggedGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.isLoggedIn() ? true : inject(Router).createUrlTree(['/login']);
};
