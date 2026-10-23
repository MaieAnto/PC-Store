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

@Component({
  selector: 'app-carrello',
  imports: [RouterLink, MatCardModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './carrello.html',
  styleUrl: './carrello.css',
})
export class Carrello implements OnInit {

  private readonly carrelloService = inject(CarrelloService);
  private readonly ordineService = inject(OrdineService);
  private readonly autenticazioneService = inject(AutenticazioneService);
  private readonly router = inject(Router);

  protected readonly utente = this.autenticazioneService.utenteCorrente;

  protected readonly righe = signal<RigaCarrello[]>([]); // righe scaricate dal backend
  protected readonly caricamento = signal(false);        // true mentre aspettiamo il backend
  protected readonly errore = signal('');                // messaggio di errore ('' = nessuno)

  protected readonly confermaVisibile = signal(false);
  protected readonly invioOrdine = signal(false);
  protected readonly erroreOrdine = signal('');

  protected readonly totale = computed(() =>
    this.righe().reduce((somma, r) => somma + r.prodotto.prezzo * r.quantita, 0)
  );
  protected readonly numeroArticoli = computed(() =>
    this.righe().reduce((somma, r) => somma + r.quantita, 0)
  );

  ngOnInit(): void {
    const utente = this.utente();
    if (!utente) {
      this.router.navigate(['/login']);
      return;
    }
    this.carica();
  }

  aumenta(riga: RigaCarrello): void {
    if (riga.quantita >= riga.prodotto.quantita) {
      return; // già al massimo disponibile
    }
    this.impostaQuantita(riga, riga.quantita + 1);
  }

  diminuisci(riga: RigaCarrello): void {
    if (riga.quantita <= 1) {
      return;
    }
    this.impostaQuantita(riga, riga.quantita - 1);
  }

  rimuovi(riga: RigaCarrello): void {
    const utente = this.utente();
    if (!utente) {
      return;
    }
    this.errore.set('');
    this.carrelloService.rimuovi(riga.prodotto.id).subscribe({
      next: (carrello) => this.righe.set(carrello.dettagli ?? []),
      error: () => this.errore.set('Impossibile rimuovere il prodotto.'),
    });
  }

  svuota(): void {
    const utente = this.utente();
    if (!utente) {
      return;
    }
    this.errore.set('');
    this.carrelloService.svuota().subscribe({
      next: (carrello) => this.righe.set(carrello.dettagli ?? []),
      error: () => this.errore.set('Impossibile svuotare il carrello.'),
    });
  }

  procediOrdine(): void {
    if (this.righe().length === 0) {
      return;
    }
    this.erroreOrdine.set('');
    this.confermaVisibile.set(true);
  }

  annullaOrdine(): void {
    this.confermaVisibile.set(false);
  }

  confermaOrdine(): void {
    const utente = this.utente();
    if (!utente) {
      return;
    }
    this.invioOrdine.set(true);
    this.erroreOrdine.set('');
    this.ordineService.creaOrdine().subscribe({
      next: () => {
        this.invioOrdine.set(false);
        this.confermaVisibile.set(false);
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

  private impostaQuantita(riga: RigaCarrello, nuovaQuantita: number): void {
    const utente = this.utente();
    if (!utente) {
      return;
    }
    this.errore.set('');
    this.carrelloService.aggiornaQuantita(riga.prodotto.id, nuovaQuantita).subscribe({
      next: (carrello) => this.righe.set(carrello.dettagli ?? []),
      error: (risposta) =>
        this.errore.set(risposta?.error?.messaggio ?? 'Impossibile aggiornare la quantità.'),
    });
  }

  private carica(): void {
    this.caricamento.set(true);
    this.errore.set('');
    this.carrelloService.getDettagli().subscribe({
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
