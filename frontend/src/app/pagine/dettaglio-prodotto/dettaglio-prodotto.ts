import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProdottoService } from '../../servizi/prodotto.service';
import { RecensioneService } from '../../servizi/recensione.service';
import { CarrelloService } from '../../servizi/carrello.service';
import { AutenticazioneService } from '../../servizi/autenticazione.service';
import { Prodotto } from '../../models/prodotto';
import { Recensione } from '../../models/recensione';
import { Ruolo } from '../../models/utente';

@Component({
  selector: 'app-dettaglio-prodotto',
  imports: [
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './dettaglio-prodotto.html',
  styleUrl: './dettaglio-prodotto.css',
})
export class DettaglioProdotto implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly prodottoService = inject(ProdottoService);
  private readonly recensioneService = inject(RecensioneService);
  private readonly carrelloService = inject(CarrelloService);
  private readonly autenticazioneService = inject(AutenticazioneService);

  protected readonly utente = this.autenticazioneService.utenteCorrente;

  protected readonly prodotto = signal<Prodotto | null>(null);
  protected readonly recensioni = signal<Recensione[]>([]);
  protected readonly media = signal(0);
  protected readonly caricamento = signal(false);
  protected readonly errore = signal('');

  protected readonly quantitaScelta = signal(1);
  protected readonly messaggioCarrello = signal('');

  protected readonly votoScelto = signal(0);
  protected readonly commentoTesto = signal('');
  protected readonly invioRecensione = signal(false);
  protected readonly erroreRecensione = signal('');
  protected readonly messaggioRecensione = signal('');

  protected readonly recensioneInModifica = signal<number | null>(null);
  protected readonly votoModifica = signal(0);
  protected readonly commentoModifica = signal('');
  protected readonly erroreModifica = signal('');
  protected readonly salvataggioModifica = signal(false);
  protected readonly recensioneDaEliminare = signal<number | null>(null);
  protected readonly eliminazioneInCorso = signal(false);

  protected readonly stelleVoto = [1, 2, 3, 4, 5];
  protected readonly mediaArrotondata = computed(() => Math.round(this.media() * 10) / 10);
  protected readonly eCliente = computed(() => this.utente()?.ruolo === Ruolo.CLIENTE);

  protected readonly giaRecensito = computed(() => {
    const utente = this.utente();
    return !!utente && this.recensioni().some((r) => r.idAutore === utente.id);
  });

  // All'avvio: legge l'id dall'URL e carica prodotto e recensioni.
  ngOnInit(): void {
    const idTesto = this.route.snapshot.paramMap.get('id');
    const id = Number(idTesto);
    if (!idTesto || Number.isNaN(id)) {
      this.errore.set('Prodotto non valido.');
      return;
    }
    this.carica(id);
  }

  // Aumenta la quantita' da acquistare (entro le scorte).
  aumenta(): void {
    const p = this.prodotto();
    if (p && this.quantitaScelta() < p.quantita) {
      this.quantitaScelta.set(this.quantitaScelta() + 1);
    }
  }

  // Diminuisce la quantita' da acquistare (minimo 1).
  diminuisci(): void {
    if (this.quantitaScelta() > 1) {
      this.quantitaScelta.set(this.quantitaScelta() - 1);
    }
  }

  // Aggiunge al carrello la quantita' scelta del prodotto
  aggiungiAlCarrello(): void {
    const p = this.prodotto();
    const utente = this.utente();
    if (!p) {
      return;
    }
    if (!utente) {
      this.messaggioCarrello.set('Devi effettuare il login per usare il carrello.');
      return;
    }
    this.messaggioCarrello.set('');
    this.carrelloService.aggiungi(p.id, this.quantitaScelta()).subscribe({
      next: () =>
        this.messaggioCarrello.set(`${this.quantitaScelta()} × "${p.nome}" aggiunto al carrello.`),
      error: (risposta) =>
        this.messaggioCarrello.set(risposta?.error?.messaggio ?? 'Impossibile aggiungere al carrello. Riprova.'),
    });
  }

  // Imposta il voto per la nuova recensione.
  impostaVoto(voto: number): void {
    this.votoScelto.set(voto);
    this.erroreRecensione.set('');
  }

  // Invia una nuova recensione del prodotto e ricarica l'elenco.
  inviaRecensione(): void {
    const p = this.prodotto();
    const utente = this.utente();
    if (!p || !utente) {
      return;
    }
    if (this.votoScelto() < 1) {
      this.erroreRecensione.set('Seleziona un voto da 1 a 5.');
      return;
    }

    this.invioRecensione.set(true);
    this.erroreRecensione.set('');
    this.messaggioRecensione.set('');

    this.recensioneService
      .aggiungiRecensione(p.id, this.votoScelto(), this.commentoTesto())
      .subscribe({
        next: () => {
          this.invioRecensione.set(false);
          this.messaggioRecensione.set('Grazie! La tua recensione è stata pubblicata.');
          this.votoScelto.set(0);
          this.commentoTesto.set('');
          this.caricaRecensioni(p.id);
        },
        error: (risposta) => {
          this.invioRecensione.set(false);
          // Il backend può rispondere con un messaggio (non acquistato, già
          // recensito, voto fuori range).
          this.erroreRecensione.set(
            risposta?.error?.messaggio ?? 'Impossibile pubblicare la recensione. Riprova.'
          );
        },
      });
  }

  /* true se la recensione appartiene all'utente corrente */
  eMia(recensione: Recensione): boolean {
    const utente = this.utente();
    return !!utente && recensione.idAutore === utente.id;
  }

  /* Apre il form di modifica precompilato con i valori attuali della recensione. */
  iniziaModifica(recensione: Recensione): void {
    this.recensioneDaEliminare.set(null);
    this.recensioneInModifica.set(recensione.id);
    this.votoModifica.set(recensione.valutazione);
    this.commentoModifica.set(recensione.commento ?? '');
    this.erroreModifica.set('');
  }

  // Chiude il form di modifica senza salvare.
  annullaModifica(): void {
    this.recensioneInModifica.set(null);
    this.erroreModifica.set('');
  }

  // Imposta il voto durante la modifica di una recensione.
  impostaVotoModifica(voto: number): void {
    this.votoModifica.set(voto);
    this.erroreModifica.set('');
  }

  // Salva le modifiche a una propria recensione e ricarica l'elenco.
  salvaModifica(idRecensione: number): void {
    const p = this.prodotto();
    if (!p) {
      return;
    }
    if (this.votoModifica() < 1) {
      this.erroreModifica.set('Seleziona un voto da 1 a 5.');
      return;
    }

    this.salvataggioModifica.set(true);
    this.erroreModifica.set('');

    this.recensioneService
      .aggiornaRecensione(idRecensione, this.votoModifica(), this.commentoModifica())
      .subscribe({
        next: () => {
          this.salvataggioModifica.set(false);
          this.recensioneInModifica.set(null);
          this.messaggioRecensione.set('Recensione aggiornata.');
          this.caricaRecensioni(p.id);
        },
        error: (risposta) => {
          this.salvataggioModifica.set(false);
          this.erroreModifica.set(
            risposta?.error?.messaggio ?? 'Impossibile aggiornare la recensione. Riprova.'
          );
        },
      });
  }

  // Chiede conferma prima di eliminare una propria recensione.
  chiediEliminazione(recensione: Recensione): void {
    this.recensioneInModifica.set(null);
    this.recensioneDaEliminare.set(recensione.id);
  }

  // Annulla la richiesta di eliminazione.
  annullaEliminazione(): void {
    this.recensioneDaEliminare.set(null);
  }

  // Elimina definitivamente la recensione e ricarica l'elenco.
  confermaEliminazione(idRecensione: number): void {
    const p = this.prodotto();
    if (!p) {
      return;
    }
    this.eliminazioneInCorso.set(true);
    this.recensioneService.eliminaRecensione(idRecensione).subscribe({
      next: () => {
        this.eliminazioneInCorso.set(false);
        this.recensioneDaEliminare.set(null);
        this.messaggioRecensione.set('Recensione eliminata.');
        this.caricaRecensioni(p.id);
      },
      error: (risposta) => {
        this.eliminazioneInCorso.set(false);
        this.recensioneDaEliminare.set(null);
        this.erroreRecensione.set(
          risposta?.error?.messaggio ?? 'Impossibile eliminare la recensione. Riprova.'
        );
      },
    });
  }

  stelle(valutazione: number): string {
    const piene = Math.max(0, Math.min(5, Math.round(valutazione)));
    return '★'.repeat(piene) + '☆'.repeat(5 - piene);
  }

  dataLeggibile(valore: string | number): string {
    const data = new Date(valore);
    return Number.isNaN(data.getTime()) ? '' : data.toLocaleDateString('it-IT');
  }

  // Scarica dal backend il prodotto e le sue recensioni.
  private carica(id: number): void {
    this.caricamento.set(true);
    this.errore.set('');

    this.prodottoService.trovaPerId(id).subscribe({
      next: (prodotto) => {
        this.prodotto.set(prodotto);
        this.caricamento.set(false);
        this.caricaRecensioni(id);
      },
      error: () => {
        this.prodotto.set(null);
        this.errore.set('Prodotto non trovato o backend non raggiungibile.');
        this.caricamento.set(false);
      },
    });
  }

  // Scarica recensioni e media delle valutazioni di un prodotto.
  private caricaRecensioni(id: number): void {
    this.recensioneService.recensioniProdotto(id).subscribe({
      next: (elenco) => this.recensioni.set(elenco),
      error: () => this.recensioni.set([]),
    });
    this.recensioneService.mediaValutazioni(id).subscribe({
      next: (media) => this.media.set(media),
      error: () => this.media.set(0),
    });
  }
}
