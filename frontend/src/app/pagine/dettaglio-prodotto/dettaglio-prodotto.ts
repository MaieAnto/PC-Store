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

  protected readonly prodotto = signal<Prodotto | null>(null); // il prodotto mostrato
  protected readonly recensioni = signal<Recensione[]>([]);    // recensioni del prodotto
  protected readonly media = signal(0);                        // media valutazioni
  protected readonly caricamento = signal(false);              // true mentre aspettiamo il backend
  protected readonly errore = signal('');                      // messaggio di errore ('' = nessuno)

  protected readonly quantitaScelta = signal(1);
  protected readonly messaggioCarrello = signal('');

  protected readonly votoScelto = signal(0);
  protected readonly commentoTesto = signal('');
  protected readonly invioRecensione = signal(false);
  protected readonly erroreRecensione = signal('');
  protected readonly messaggioRecensione = signal('');

  protected readonly stelleVoto = [1, 2, 3, 4, 5];
  protected readonly mediaArrotondata = computed(() => Math.round(this.media() * 10) / 10);
  protected readonly eCliente = computed(() => this.utente()?.ruolo === Ruolo.CLIENTE);

  protected readonly giaRecensito = computed(() => {
    const utente = this.utente();
    return !!utente && this.recensioni().some((r) => r.autore?.id === utente.id);
  });

  ngOnInit(): void {
    const idTesto = this.route.snapshot.paramMap.get('id');
    const id = Number(idTesto);
    if (!idTesto || Number.isNaN(id)) {
      this.errore.set('Prodotto non valido.');
      return;
    }
    this.carica(id);
  }

  aumenta(): void {
    const p = this.prodotto();
    if (p && this.quantitaScelta() < p.quantita) {
      this.quantitaScelta.set(this.quantitaScelta() + 1);
    }
  }

  diminuisci(): void {
    if (this.quantitaScelta() > 1) {
      this.quantitaScelta.set(this.quantitaScelta() - 1);
    }
  }

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

  impostaVoto(voto: number): void {
    this.votoScelto.set(voto);
    this.erroreRecensione.set('');
  }

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

  stelle(valutazione: number): string {
    const piene = Math.max(0, Math.min(5, Math.round(valutazione)));
    return '★'.repeat(piene) + '☆'.repeat(5 - piene);
  }

  dataLeggibile(valore: string | number): string {
    const data = new Date(valore);
    return Number.isNaN(data.getTime()) ? '' : data.toLocaleDateString('it-IT');
  }

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
