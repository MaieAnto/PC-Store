import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { ProdottoService } from '../../servizi/prodotto.service';
import { CarrelloService } from '../../servizi/carrello.service';
import { AutenticazioneService } from '../../servizi/autenticazione.service';
import { CATEGORIE, FiltriCatalogo, Prodotto } from '../../modelli/prodotto';
import { Ruolo } from '../../modelli/utente';

@Component({
  selector: 'app-catalogo',
  imports: [
    RouterLink,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatBadgeModule,
  ],
  templateUrl: './catalogo.html',
  styleUrl: './catalogo.css',
})
export class Catalogo implements OnInit, OnDestroy {
  private readonly prodottoService = inject(ProdottoService);
  private readonly carrelloService = inject(CarrelloService);
  private readonly autenticazioneService = inject(AutenticazioneService);
  private readonly router = inject(Router);

  protected readonly utente = this.autenticazioneService.utenteCorrente;

  protected readonly eCliente = computed(() => this.utente()?.ruolo === Ruolo.CLIENTE);

  protected readonly categorie = CATEGORIE;

  protected readonly DIMENSIONE_PAGINA = 12;

  protected readonly prodotti = signal<Prodotto[]>([]); // i prodotti della SOLA pagina corrente
  protected readonly totale = signal(0); // quanti prodotti in tutto rispettano i filtri
  protected readonly pagina = signal(0); // indice della pagina corrente
  protected readonly caricamento = signal(false); // true mentre aspettiamo il backend
  protected readonly errore = signal(''); // messaggio di errore ('' = nessuno)

  protected readonly numeroCarrello = signal(0); // quanti articoli ci sono nel carrello
  protected readonly messaggioCarrello = signal(''); // breve conferma dopo un "aggiungi"
  protected readonly idInAggiunta = signal<number | null>(null); // id del prodotto per cui e' in corso una chiamata

  protected readonly indiceCategoria = signal(0); // posizione nella lista CATEGORIE (0 = tutte)
  protected readonly testoRicerca = signal(''); // testo cercato nel nome
  protected readonly marcaFiltro = signal(''); // testo cercato nella marca
  protected readonly prezzoMin = signal(''); // prezzo minimo
  protected readonly prezzoMax = signal(''); // prezzo massimo

  protected readonly ordinamento = signal('nome-asc');

  // Opzioni del menu "Ordina per"
  protected readonly opzioniOrdinamento = [
    { valore: 'nome-asc', etichetta: 'Nome (A–Z)' },
    { valore: 'nome-desc', etichetta: 'Nome (Z–A)' },
    { valore: 'prezzo-asc', etichetta: 'Prezzo (crescente)' },
    { valore: 'prezzo-desc', etichetta: 'Prezzo (decrescente)' },
    { valore: 'marca-asc', etichetta: 'Marca (A–Z)' },
  ];

  private timerRicerca?: ReturnType<typeof setTimeout>;

  // carica la prima pagina del catalogo e il numero di articoli nel carrello.
  ngOnInit(): void {
    this.carica();
    this.aggiornaNumeroCarrello();
  }

  ngOnDestroy(): void {
    clearTimeout(this.timerRicerca);
  }

  /* dimentica l'utente corrente e torna al login. */
  logout(): void {
    this.autenticazioneService.logout();
    this.router.navigate(['/login']);
  }

  /* Cambia la categoria selezionata */
  cambiaCategoria(indice: number): void {
    this.indiceCategoria.set(indice);
    this.applicaFiltri();
  }

  /* Cambia l'ordinamento */
  cambiaOrdinamento(valore: string): void {
    this.ordinamento.set(valore);
    this.applicaFiltri();
  }

  /* Aggiorna un filtro di testo (nome/marca/prezzo) e ricarica con un piccolo
     ritardo, cosi' non partono richieste a ogni singolo tasto. */
  aggiornaTesto(campo: 'nome' | 'marca' | 'min' | 'max', valore: string): void {
    if (campo === 'nome') this.testoRicerca.set(valore);
    else if (campo === 'marca') this.marcaFiltro.set(valore);
    else if (campo === 'min') this.prezzoMin.set(valore);
    else this.prezzoMax.set(valore);

    clearTimeout(this.timerRicerca);
    this.timerRicerca = setTimeout(() => this.applicaFiltri(), 350);
  }

  /* Azzera tutti i filtri e ricarica dalla prima pagina. */
  azzeraFiltri(): void {
    clearTimeout(this.timerRicerca);
    this.testoRicerca.set('');
    this.marcaFiltro.set('');
    this.prezzoMin.set('');
    this.prezzoMax.set('');
    this.indiceCategoria.set(0);
    this.ordinamento.set('nome-asc');
    this.applicaFiltri();
  }

  /* Evento del mat-paginator: l'utente ha cambiato pagina. */
  cambiaPagina(evento: PageEvent): void {
    this.pagina.set(evento.pageIndex);
    this.carica();
  }

  /* Un filtro/ordinamento e' cambiato: si riparte sempre dalla prima pagina. */
  private applicaFiltri(): void {
    this.pagina.set(0);
    this.carica();
  }

  /*
   * Pulsante "Aggiungi al carrello": aggiunge 1 pezzo del prodotto al carrello
   * dell'utente loggato; alla risposta aggiorna il contatore e mostra un breve
   * messaggio di conferma.
   */
  aggiungiAlCarrello(prodotto: Prodotto): void {
    const utente = this.utente();
    if (!utente) {
      this.messaggioCarrello.set('Devi effettuare il login per usare il carrello.');
      return;
    }

    this.messaggioCarrello.set('');
    this.idInAggiunta.set(prodotto.id);

    this.carrelloService.aggiungi(utente.id, prodotto.id, 1).subscribe({
      next: (carrello) => {
        this.numeroCarrello.set(this.contaArticoli(carrello.dettagli ?? []));
        this.messaggioCarrello.set(`"${prodotto.nome}" aggiunto al carrello.`);
        this.idInAggiunta.set(null);
      },
      error: (risposta) => {
        this.messaggioCarrello.set(
          risposta?.error?.messaggio ?? 'Impossibile aggiungere al carrello. Riprova.',
        );
        this.idInAggiunta.set(null);
      },
    });
  }

  /*
   * Converte il testo di un campo prezzo in numero.
   * Campo vuoto o testo non numerico -> null, cioe' "nessun limite".
   */
  private numeroOppureNull(testo: string): number | null {
    const pulito = testo.trim();
    if (pulito === '') {
      return null;
    }
    const numero = Number(pulito);
    return Number.isNaN(numero) ? null : numero;
  }

  /*
   * Somma le quantita' di tutte le righe: e' il numero di articoli nel carrello.
   */
  private contaArticoli(righe: { quantita: number }[]): number {
    return righe.reduce((somma, r) => somma + r.quantita, 0);
  }

  /*
   * Chiede al backend le righe del carrello per aggiornare il badge in alto.
   * Se l'utente non e' loggato o la chiamata fallisce, il badge resta a 0.
   */
  private aggiornaNumeroCarrello(): void {
    const utente = this.utente();
    if (!utente) {
      this.numeroCarrello.set(0);
      return;
    }
    this.carrelloService.getDettagli(utente.id).subscribe({
      next: (righe) => this.numeroCarrello.set(this.contaArticoli(righe)),
      error: () => this.numeroCarrello.set(0),
    });
  }

  /*
   * Chiede al backend UNA pagina del catalogo, con i filtri e l'ordinamento
   * correnti. Il filtraggio e la paginazione avvengono interamente lato server.
   */
  private carica(): void {
    this.caricamento.set(true);
    this.errore.set('');

    // Categoria selezionata: la prima voce ("Tutte le categorie") ha
    // endpoint null e significa "nessun filtro di categoria".
    const categoriaScelta = this.categorie[this.indiceCategoria()];
    const [ordinaPer, direzione] = this.ordinamento().split('-');

    const filtri: FiltriCatalogo = {
      nome: this.testoRicerca().trim() || undefined,
      marca: this.marcaFiltro().trim() || undefined,
      categoria: categoriaScelta.endpoint === null ? undefined : categoriaScelta.etichetta,
      min: this.numeroOppureNull(this.prezzoMin()),
      max: this.numeroOppureNull(this.prezzoMax()),
      pagina: this.pagina(),
      dimensione: this.DIMENSIONE_PAGINA,
      ordinaPer,
      direzione,
    };

    this.prodottoService.cercaCatalogo(filtri).subscribe({
      next: (risultato) => {
        this.prodotti.set(risultato.content);
        this.totale.set(risultato.totaleElementi);
        this.caricamento.set(false);
      },
      error: () => {
        this.prodotti.set([]);
        this.totale.set(0);
        this.errore.set(
          'Impossibile caricare i prodotti. Verifica che il backend sia in esecuzione.',
        );
        this.caricamento.set(false);
      },
    });
  }
}
