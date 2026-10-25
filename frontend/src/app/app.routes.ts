import { Routes } from '@angular/router';
import { Layout } from './layout/layout';
import { Login } from './pagine/login/login';
import { Registrazione } from './pagine/registrazione/registrazione';
import { Catalogo } from './pagine/catalogo/catalogo';
import { DettaglioProdotto } from './pagine/dettaglio-prodotto/dettaglio-prodotto';
import { Carrello } from './pagine/carrello/carrello';
import { Ordini } from './pagine/ordini/ordini';
import { DashboardFornitore } from './pagine/dashboard-fornitore/dashboard-fornitore';
import { AggiungiProdotto } from './pagine/aggiungi-prodotto/aggiungi-prodotto';
import { GestioneProdotti } from './pagine/gestione-prodotti/gestione-prodotti';
import { Rifornimento } from './pagine/rifornimento/rifornimento';
import { Rifornimenti } from './pagine/rifornimenti/rifornimenti';
import { ruoloGuard } from './guard/autenticazione.guard';
import { Ruolo } from './models/utente';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'registrazione', component: Registrazione },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '', component: Layout,
    children: [
      // Catalogo e dettaglio prodotto sono navigabili anche senza login.
      { path: 'catalogo', component: Catalogo },
      { path: 'prodotto/:id', component: DettaglioProdotto },
      // Area cliente
      { path: 'carrello', component: Carrello, canActivate: [ruoloGuard(Ruolo.CLIENTE)] },
      { path: 'ordini', component: Ordini, canActivate: [ruoloGuard(Ruolo.CLIENTE)] },
      // Area fornitore
      { path: 'fornitore', component: DashboardFornitore, canActivate: [ruoloGuard(Ruolo.FORNITORE)] },
      { path: 'aggiungi-prodotto', component: AggiungiProdotto, canActivate: [ruoloGuard(Ruolo.FORNITORE)] },
      { path: 'gestione-prodotti', component: GestioneProdotti, canActivate: [ruoloGuard(Ruolo.FORNITORE)] },
      { path: 'rifornimento', component: Rifornimento, canActivate: [ruoloGuard(Ruolo.FORNITORE)] },
      { path: 'rifornimenti', component: Rifornimenti, canActivate: [ruoloGuard(Ruolo.FORNITORE)] },
    ],
  },

  { path: '**', redirectTo: 'login' },
];
