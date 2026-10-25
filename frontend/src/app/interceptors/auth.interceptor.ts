import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AutenticazioneService } from '../servizi/autenticazione.service';

/* Aggiunge l'header Authorization con il token JWT a ogni richiesta, se presente. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AutenticazioneService).getToken();

  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    });
  }

  return next(req);
};
