import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
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
  ],
  templateUrl: './catalogo.html',
  styleUrl: './catalogo.css',
})
export class Catalogo implements OnInit {
  private readonly prodottoService = inject(ProdottoService);
  private readonly carrelloService = inject(CarrelloService);
  private readonly autenticazioneService = inject(AutenticazioneService);

  protected readonly utente = this.autenticazioneService.utenteCorrente;

  protected readonly eCliente = computed(() => this.utente()?.ruolo === Ruolo.CLIENTE);

  protected readonly categorie = CATEGORIE;

  protected readonly DIMENSIONE_PAGINA = 12;

  protected readonly prodotti = signal<Prodotto[]>([]); // i prodotti della SOLA pagina corrente
  protected readonly totale = signal(0); // quanti prodotti in tutto rispettano i filtri
  protected readonly pagina = signal(0); // indice della pagina corrente
  protected readonly caricamento = signal(false); // true mentre aspettiamo il backend
  protected readonly errore = signal(''); // messaggio di errore ('' = nessuno)

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

  // carica la prima pagina del catalogo.
  ngOnInit(): void {
    this.carica();
  }

  cambiaCategoria(indice: number): void {
    this.indiceCategoria.set(indice);
  }

  cambiaOrdinamento(valore: string): void {
    this.ordinamento.set(valore);
  }

  aggiornaTesto(campo: 'nome' | 'marca' | 'min' | 'max', valore: string): void {
    if (campo === 'nome') this.testoRicerca.set(valore);
    else if (campo === 'marca') this.marcaFiltro.set(valore);
    else if (campo === 'min') this.prezzoMin.set(valore);
    else this.prezzoMax.set(valore);
  }

  /* Pulsante "Cerca": applica i filtri correnti e riparte dalla prima pagina. */
  cerca(): void {
    this.pagina.set(0);
    this.carica();
  }

  /* Azzera tutti i filtri e ricarica dalla prima pagina. */
  azzeraFiltri(): void {
    this.testoRicerca.set('');
    this.marcaFiltro.set('');
    this.prezzoMin.set('');
    this.prezzoMax.set('');
    this.indiceCategoria.set(0);
    this.ordinamento.set('nome-asc');
    this.cerca();
  }

  cambiaPagina(evento: PageEvent): void {
    this.pagina.set(evento.pageIndex);
    this.carica();
  }

  aggiungiAlCarrello(prodotto: Prodotto): void {
    const utente = this.utente();
    if (!utente) {
      this.messaggioCarrello.set('Devi effettuare il login per usare il carrello.');
      return;
    }

    this.messaggioCarrello.set('');
    this.idInAggiunta.set(prodotto.id);

    this.carrelloService.aggiungi(utente.id, prodotto.id, 1).subscribe({
      next: () => {
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

  /* Converto il testo di un campo prezzo in numero  */
  private numeroOppureNull(testo: string): number | null {
    const pulito = testo.trim();
    if (pulito === '') {
      return null;
    }
    const numero = Number(pulito);
    return Number.isNaN(numero) ? null : numero;
  }

  /* Chiede al backend una pagina del catalogo, con i filtri e l'ordinamento correnti */
  private carica(): void {
    this.caricamento.set(true);
    this.errore.set('');

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
