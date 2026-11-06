import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { API_URL } from './api.service';
import { AuthService } from './auth.service';

/** Ajoute le jeton JWT aux appels API et renvoie vers /login si la session a expiré. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const token = auth.token;
  const request = token && req.url.startsWith(API_URL)
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(request).pipe(
    catchError((err: HttpErrorResponse) => {
      const isLogin = req.url.includes('/auth/login');
      if (err.status === 401 && !isLogin && auth.isLoggedIn()) {
        auth.logout();
        router.navigate(['/login']);
      }
      return throwError(() => err);
    }),
  );
};
