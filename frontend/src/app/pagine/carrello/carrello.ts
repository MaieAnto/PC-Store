import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AutenticazioneService } from '../../servizi/autenticazione.service';
import { CarrelloService } from '../../servizi/carrello.service';
import { OrdineService } from '../../servizi/ordine.service';
import { RigaCarrello } from '../../modelli/carrello';

/*
 * Componente della schermata del Carrello.
 *
 * Ci arriva il CLIENTE dopo aver aggiunto dei prodotti dal catalogo. Mostra
 * l'elenco delle righe (prodotto + quantità), permette di aumentare/diminuire
 * la quantità, rimuovere una riga o svuotare tutto, e calcola il totale.
 * La grafica usa Angular Material.
 */
@Component({
  selector: 'app-carrello',
  imports: [RouterLink, MatCardModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './carrello.html',
  styleUrl: './carrello.css',
})
export class Carrello implements OnInit {
  // Service e strumenti ottenuti con inject() (come nelle altre pagine).
  private readonly carrelloService = inject(CarrelloService);
  private readonly ordineService = inject(OrdineService);
  private readonly autenticazioneService = inject(AutenticazioneService);
  private readonly router = inject(Router);

  // Utente loggato (signal): da qui prendiamo l'id per le chiamate.
  protected readonly utente = this.autenticazioneService.utenteCorrente;

  /*
   * Stato della pagina con i signal (variabili reattive: quando cambiano, la
   * schermata si aggiorna da sola).
   */
  protected readonly righe = signal<RigaCarrello[]>([]); // righe scaricate dal backend
  protected readonly caricamento = signal(false);        // true mentre aspettiamo il backend
  protected readonly errore = signal('');                // messaggio di errore ('' = nessuno)

  // Checkout: pannello di conferma visibile, invio in corso, errore dell'ordine.
  protected readonly confermaVisibile = signal(false);
  protected readonly invioOrdine = signal(false);
  protected readonly erroreOrdine = signal('');

  /*
   * Valori CALCOLATI dalle righe (computed): Angular li ricalcola da solo ogni
   * volta che cambia l'elenco righe.
   *  - totale: somma di prezzo * quantità di ogni riga.
   *  - numeroArticoli: somma delle quantità (quanti pezzi in tutto).
   */
  protected readonly totale = computed(() =>
    this.righe().reduce((somma, r) => somma + r.prodotto.prezzo * r.quantita, 0)
  );
  protected readonly numeroArticoli = computed(() =>
    this.righe().reduce((somma, r) => somma + r.quantita, 0)
  );

  /*
   * ngOnInit viene chiamato una volta alla creazione della pagina: controlliamo
   * che ci sia un utente loggato e carichiamo il suo carrello.
   */
  ngOnInit(): void {
    const utente = this.utente();
    if (!utente) {
      // Nessun utente: non possiamo mostrare un carrello, torniamo al login.
      this.router.navigate(['/login']);
      return;
    }
    this.carica(utente.id);
  }

  /*
   * Aumenta di 1 la quantità di una riga, senza superare la giacenza in
   * magazzino del prodotto (prodotto.quantita).
   */
  aumenta(riga: RigaCarrello): void {
    if (riga.quantita >= riga.prodotto.quantita) {
      return; // già al massimo disponibile
    }
    this.impostaQuantita(riga, riga.quantita + 1);
  }

  /*
   * Diminuisce di 1 la quantità di una riga. Non si scende sotto 1: per
   * togliere del tutto un prodotto si usa il pulsante "Rimuovi".
   */
  diminuisci(riga: RigaCarrello): void {
    if (riga.quantita <= 1) {
      return;
    }
    this.impostaQuantita(riga, riga.quantita - 1);
  }

  /*
   * Rimuove completamente una riga dal carrello.
   */
  rimuovi(riga: RigaCarrello): void {
    const utente = this.utente();
    if (!utente) {
      return;
    }
    this.errore.set('');
    this.carrelloService.rimuovi(utente.id, riga.prodotto.id).subscribe({
      next: (carrello) => this.righe.set(carrello.dettagli ?? []),
      error: () => this.errore.set('Impossibile rimuovere il prodotto.'),
    });
  }

  /*
   * Svuota tutto il carrello.
   */
  svuota(): void {
    const utente = this.utente();
    if (!utente) {
      return;
    }
    this.errore.set('');
    this.carrelloService.svuota(utente.id).subscribe({
      next: (carrello) => this.righe.set(carrello.dettagli ?? []),
      error: () => this.errore.set('Impossibile svuotare il carrello.'),
    });
  }

  /*
   * Pulsante "Procedi all'ordine": non ordina subito, mostra prima un pannello
   * di conferma (per evitare ordini accidentali).
   */
  procediOrdine(): void {
    if (this.righe().length === 0) {
      return;
    }
    this.erroreOrdine.set('');
    this.confermaVisibile.set(true);
  }

  /* Annulla la conferma e torna al carrello. */
  annullaOrdine(): void {
    this.confermaVisibile.set(false);
  }

  /*
   * Conferma dell'ordine: chiama il backend che trasforma il carrello in ordine
   * (POST /api/ordini/{idUtente}). Alla riuscita andiamo alla pagina degli ordini
   * (il carrello risulta svuotato dal backend).
   */
  confermaOrdine(): void {
    const utente = this.utente();
    if (!utente) {
      return;
    }
    this.invioOrdine.set(true);
    this.erroreOrdine.set('');
    this.ordineService.creaOrdine(utente.id).subscribe({
      next: () => {
        this.invioOrdine.set(false);
        this.confermaVisibile.set(false);
        this.router.navigate(['/ordini']);
      },
      error: (risposta) => {
        this.invioOrdine.set(false);
        // Il backend può rispondere con un messaggio (es. scorta insufficiente).
        this.erroreOrdine.set(
          risposta?.error?.messaggio ?? 'Impossibile completare l\'ordine. Riprova.'
        );
      },
    });
  }

  /*
   * Imposta una quantità precisa per la riga chiamando il backend, poi
   * aggiorna l'elenco con il carrello restituito.
   */
  private impostaQuantita(riga: RigaCarrello, nuovaQuantita: number): void {
    const utente = this.utente();
    if (!utente) {
      return;
    }
    this.errore.set('');
    this.carrelloService.aggiornaQuantita(utente.id, riga.prodotto.id, nuovaQuantita).subscribe({
      next: (carrello) => this.righe.set(carrello.dettagli ?? []),
      error: (risposta) =>
        this.errore.set(risposta?.error?.messaggio ?? 'Impossibile aggiornare la quantità.'),
    });
  }

  /*
   * Scarica le righe del carrello dell'utente dal backend.
   */
  private carica(idUtente: number): void {
    this.caricamento.set(true);
    this.errore.set('');
    this.carrelloService.getDettagli(idUtente).subscribe({
      next: (righe) => {
        this.righe.set(righe);
        this.caricamento.set(false);
      },
      error: () => {
        this.righe.set([]);
        this.errore.set('Impossibile caricare il carrello. Verifica che il backend sia in esecuzione.');
        this.caricamento.set(false);
      },
    });
  }
}
