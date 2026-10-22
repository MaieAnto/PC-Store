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
import { Prodotto } from '../../modelli/prodotto';
import { Recensione } from '../../modelli/recensione';
import { Ruolo } from '../../modelli/utente';

/*
 * Componente della card di DETTAGLIO di un prodotto.
 *
 * Ci si arriva dal catalogo cliccando immagine o nome (rotta /prodotto/:id).
 * Mostra tutti i dati del prodotto, i suoi dettagli tecnici, un selettore
 * di quantità per aggiungerlo al carrello e le recensioni. Un cliente che ha
 * acquistato il prodotto (e non l'ha ancora recensito) può scriverne una.
 * La grafica usa Angular Material.
 */
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
  // Strumenti e service ottenuti con inject().
  private readonly route = inject(ActivatedRoute);
  private readonly prodottoService = inject(ProdottoService);
  private readonly recensioneService = inject(RecensioneService);
  private readonly carrelloService = inject(CarrelloService);
  private readonly autenticazioneService = inject(AutenticazioneService);

  // Utente loggato (signal): serve l'id per carrello e recensioni.
  protected readonly utente = this.autenticazioneService.utenteCorrente;

  /*
   * Stato della pagina (signal reattivi).
   */
  protected readonly prodotto = signal<Prodotto | null>(null); // il prodotto mostrato
  protected readonly recensioni = signal<Recensione[]>([]);    // recensioni del prodotto
  protected readonly media = signal(0);                        // media valutazioni (0 = nessuna)
  protected readonly caricamento = signal(false);              // true mentre aspettiamo il backend
  protected readonly errore = signal('');                      // messaggio di errore ('' = nessuno)

  // Quantità scelta nel selettore (parte da 1).
  protected readonly quantitaScelta = signal(1);
  // Messaggio di conferma/avviso dopo un "Aggiungi al carrello".
  protected readonly messaggioCarrello = signal('');

  /*
   * Stato del form "scrivi recensione".
   *  - votoScelto: 0 = nessun voto scelto, 1..5 = stelle selezionate.
   *  - commentoTesto: testo del commento (facoltativo).
   *  - invioRecensione: true durante l'invio.
   *  - erroreRecensione / messaggioRecensione: esiti da mostrare.
   */
  protected readonly votoScelto = signal(0);
  protected readonly commentoTesto = signal('');
  protected readonly invioRecensione = signal(false);
  protected readonly erroreRecensione = signal('');
  protected readonly messaggioRecensione = signal('');

  // Le cinque stelle del selettore di voto (comodo per il *ngFor del template).
  protected readonly stelleVoto = [1, 2, 3, 4, 5];

  // Media arrotondata a un decimale, comoda per il template.
  protected readonly mediaArrotondata = computed(() => Math.round(this.media() * 10) / 10);

  // È un cliente? (solo i clienti possono recensire).
  protected readonly eCliente = computed(() => this.utente()?.ruolo === Ruolo.CLIENTE);

  // L'utente corrente ha già recensito questo prodotto? (compare tra gli autori).
  protected readonly giaRecensito = computed(() => {
    const utente = this.utente();
    return !!utente && this.recensioni().some((r) => r.autore?.id === utente.id);
  });

  ngOnInit(): void {
    // Leggiamo l'id dall'URL (es. /prodotto/7 -> "7") e lo convertiamo in numero.
    const idTesto = this.route.snapshot.paramMap.get('id');
    const id = Number(idTesto);
    if (!idTesto || Number.isNaN(id)) {
      this.errore.set('Prodotto non valido.');
      return;
    }
    this.carica(id);
  }

  /* Aumenta di 1 la quantità scelta, senza superare la giacenza del prodotto. */
  aumenta(): void {
    const p = this.prodotto();
    if (p && this.quantitaScelta() < p.quantita) {
      this.quantitaScelta.set(this.quantitaScelta() + 1);
    }
  }

  /* Diminuisce di 1 la quantità scelta, senza scendere sotto 1. */
  diminuisci(): void {
    if (this.quantitaScelta() > 1) {
      this.quantitaScelta.set(this.quantitaScelta() - 1);
    }
  }

  /* Aggiunge al carrello la quantità scelta del prodotto corrente. */
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
    this.carrelloService.aggiungi(utente.id, p.id, this.quantitaScelta()).subscribe({
      next: () =>
        this.messaggioCarrello.set(`${this.quantitaScelta()} × "${p.nome}" aggiunto al carrello.`),
      error: (risposta) =>
        this.messaggioCarrello.set(risposta?.error?.messaggio ?? 'Impossibile aggiungere al carrello. Riprova.'),
    });
  }

  /* Imposta il voto scelto cliccando su una stella del form. */
  impostaVoto(voto: number): void {
    this.votoScelto.set(voto);
    this.erroreRecensione.set('');
  }

  /*
   * Invia la recensione (voto + commento facoltativo). Il backend controlla che
   * l'utente abbia acquistato il prodotto e non l'abbia già recensito: eventuali
   * errori li mostriamo. Alla riuscita ricarichiamo le recensioni (così il form
   * sparisce, sostituito dall'avviso "hai già recensito").
   */
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
      .aggiungiRecensione(utente.id, p.id, this.votoScelto(), this.commentoTesto())
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

  /*
   * Restituisce una stringa di stelle piene/vuote per una valutazione da 0 a 5
   * (es. 3 -> "★★★☆☆"). Usata per mostrare i voti già presenti.
   */
  stelle(valutazione: number): string {
    const piene = Math.max(0, Math.min(5, Math.round(valutazione)));
    return '★'.repeat(piene) + '☆'.repeat(5 - piene);
  }

  /*
   * Formatta la data della recensione in modo leggibile (gg/mm/aaaa).
   * Accetta sia una data ISO sia i millisecondi.
   */
  dataLeggibile(valore: string | number): string {
    const data = new Date(valore);
    return Number.isNaN(data.getTime()) ? '' : data.toLocaleDateString('it-IT');
  }

  /*
   * Scarica dal backend il prodotto e, se va a buon fine, le sue recensioni e
   * la media delle valutazioni.
   */
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

  /*
   * Scarica recensioni e media. Se falliscono non blocchiamo la pagina: il
   * prodotto è comunque visibile, mostreremo semplicemente "nessuna recensione".
   */
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
