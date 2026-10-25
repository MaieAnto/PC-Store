import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CarrelloService } from '../../servizi/carrello.service';
import { OrdineService } from '../../servizi/ordine.service';
import { RigaCarrello } from '../../models/carrello';

@Component({
  selector: 'app-carrello',
  imports: [RouterLink, MatCardModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './carrello.html',
  styleUrl: './carrello.css',
})
export class Carrello implements OnInit {

  private readonly carrelloService = inject(CarrelloService);
  private readonly ordineService = inject(OrdineService);
  private readonly router = inject(Router);

  protected readonly righe = signal<RigaCarrello[]>([]);
  protected readonly caricamento = signal(false);
  protected readonly errore = signal('');

  protected readonly confermaVisibile = signal(false);
  protected readonly invioOrdine = signal(false);
  protected readonly erroreOrdine = signal('');

  protected readonly totale = computed(() =>
    this.righe().reduce((somma, r) => somma + r.subtotale, 0)
  );
  protected readonly numeroArticoli = computed(() =>
    this.righe().reduce((somma, r) => somma + r.quantita, 0)
  );

  // All'avvio carica il carrello
  ngOnInit(): void {
    this.carica();
  }

  // Aumenta di 1 la quantita' di una riga (entro le scorte disponibili).
  aumenta(riga: RigaCarrello): void {
    if (riga.quantita >= riga.quantitaDisponibile) {
      return;
    }
    this.impostaQuantita(riga, riga.quantita + 1);
  }

  // Diminuisce di 1 la quantita' di una riga (minimo 1).
  diminuisci(riga: RigaCarrello): void {
    if (riga.quantita <= 1) {
      return;
    }
    this.impostaQuantita(riga, riga.quantita - 1);
  }

  // Rimuove una riga dal carrello.
  rimuovi(riga: RigaCarrello): void {
    this.errore.set('');
    this.carrelloService.rimuovi(riga.idProdotto).subscribe({
      next: (carrello) => this.righe.set(carrello.righe ?? []),
      error: () => this.errore.set('Impossibile rimuovere il prodotto.'),
    });
  }

  // Svuota l'intero carrello.
  svuota(): void {
    this.errore.set('');
    this.carrelloService.svuota().subscribe({
      next: (carrello) => this.righe.set(carrello.righe ?? []),
      error: () => this.errore.set('Impossibile svuotare il carrello.'),
    });
  }

  // Mostra la finestra di conferma dell'ordine (se il carrello non e' vuoto).
  procediOrdine(): void {
    if (this.righe().length === 0) {
      return;
    }
    this.erroreOrdine.set('');
    this.confermaVisibile.set(true);
  }

  // Chiude la finestra di conferma senza ordinare.
  annullaOrdine(): void {
    this.confermaVisibile.set(false);
  }

  // Conferma l'ordine: lo crea dal carrello e porta alla pagina degli ordini.
  confermaOrdine(): void {
    this.invioOrdine.set(true);
    this.erroreOrdine.set('');
    this.ordineService.creaOrdine().subscribe({
      next: () => {
        this.invioOrdine.set(false);
        this.confermaVisibile.set(false);
        // Il carrello e' stato svuotato lato server: azzero subito il badge nell'header.
        this.carrelloService.azzeraConteggio();
        this.righe.set([]);
        this.router.navigate(['/ordini']);
      },
      error: (risposta) => {
        this.invioOrdine.set(false);
        this.erroreOrdine.set(
          risposta?.error?.messaggio ?? 'Impossibile completare l\'ordine. Riprova.'
        );
      },
    });
  }

  // Invia al backend la nuova quantita' di una riga e aggiorna la vista.
  private impostaQuantita(riga: RigaCarrello, nuovaQuantita: number): void {
    this.errore.set('');
    this.carrelloService.aggiornaQuantita(riga.idProdotto, nuovaQuantita).subscribe({
      next: (carrello) => this.righe.set(carrello.righe ?? []),
      error: (risposta) =>
        this.errore.set(risposta?.error?.messaggio ?? 'Impossibile aggiornare la quantità.'),
    });
  }

  // Scarica dal backend le righe del carrello.
  private carica(): void {
    this.caricamento.set(true);
    this.errore.set('');
    this.carrelloService.getDettagli().subscribe({
      next: (carrello) => {
        this.righe.set(carrello.righe ?? []);
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
