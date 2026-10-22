import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { OrdineService } from '../../servizi/ordine.service';
import { AutenticazioneService } from '../../servizi/autenticazione.service';
import { Ordine } from '../../modelli/ordine';

/*
 * Componente della schermata "I miei ordini".
 *
 * Mostra lo storico degli ordini del cliente (dal più recente). Ogni ordine è
 * un pannello espandibile (mat-expansion-panel) che mostra le sue righe
 * (prodotto, quantità, prezzo congelato e subtotale).
 *
 * Come per il carrello, serve l'id dell'utente loggato: lo prendiamo dal signal
 * utenteCorrente. Se non c'è nessun utente, rimandiamo al login.
 */
@Component({
  selector: 'app-ordini',
  imports: [RouterLink, MatExpansionModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './ordini.html',
  styleUrl: './ordini.css',
})
export class Ordini implements OnInit {
  private readonly ordineService = inject(OrdineService);
  private readonly autenticazioneService = inject(AutenticazioneService);
  private readonly router = inject(Router);

  // Utente loggato (signal): da qui prendiamo l'id.
  protected readonly utente = this.autenticazioneService.utenteCorrente;

  // Stato della pagina.
  protected readonly ordini = signal<Ordine[]>([]);   // storico scaricato dal backend
  protected readonly caricamento = signal(false);     // true mentre aspettiamo il backend
  protected readonly errore = signal('');             // messaggio di errore ('' = nessuno)

  ngOnInit(): void {
    const utente = this.utente();
    if (!utente) {
      this.router.navigate(['/login']);
      return;
    }
    this.carica(utente.id);
  }

  /* Totale dell'ordine = somma di prezzoUnitario * quantità di ogni riga. */
  totaleOrdine(ordine: Ordine): number {
    return ordine.dettagli.reduce((somma, r) => somma + r.prezzoUnitario * r.quantita, 0);
  }

  /* Numero di articoli dell'ordine (somma delle quantità). */
  numeroArticoli(ordine: Ordine): number {
    return ordine.dettagli.reduce((somma, r) => somma + r.quantita, 0);
  }

  /* Data/ora dell'ordine in formato leggibile. Accetta ISO o millisecondi. */
  dataLeggibile(valore: string | number): string {
    const data = new Date(valore);
    return Number.isNaN(data.getTime()) ? '' : data.toLocaleString('it-IT');
  }

  /* Scarica lo storico ordini del cliente. */
  private carica(idUtente: number): void {
    this.caricamento.set(true);
    this.errore.set('');
    this.ordineService.storicoOrdini(idUtente).subscribe({
      next: (elenco) => {
        this.ordini.set(elenco);
        this.caricamento.set(false);
      },
      error: () => {
        this.ordini.set([]);
        this.errore.set('Impossibile caricare gli ordini. Verifica che il backend sia in esecuzione.');
        this.caricamento.set(false);
      },
    });
  }
}
