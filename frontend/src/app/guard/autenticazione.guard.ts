import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AutenticazioneService } from '../servizi/autenticazione.service';
import { Ruolo } from '../models/utente';

/* Consente l'accesso solo all'utente con il ruolo richiesto */
export const ruoloGuard = (ruoloRichiesto: Ruolo): CanActivateFn => {
  return () => {
    const auth = inject(AutenticazioneService);
    const router = inject(Router);

    const utente = auth.utenteCorrente();
    if (!auth.getToken() || !utente) {
      return router.createUrlTree(['/login']);
    }
    if (utente.ruolo === ruoloRichiesto) {
      return true;
    }
    // Ruolo sbagliato: ognuno torna alla propria pagina iniziale.
    const destinazione = utente.ruolo === Ruolo.FORNITORE ? '/fornitore' : '/catalogo';
    return router.createUrlTree([destinazione]);
  };
};
