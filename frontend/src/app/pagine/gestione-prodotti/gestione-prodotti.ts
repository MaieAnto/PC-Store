import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ProdottoService } from '../../servizi/prodotto.service';
import { CATEGORIE, NuovoProdotto, Prodotto } from '../../models/prodotto';

@Component({
  selector: 'app-gestione-prodotti',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './gestione-prodotti.html',
  styleUrl: './gestione-prodotti.css',
})
export class GestioneProdotti implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly prodottoService = inject(ProdottoService);

  // Categorie selezionabili
  protected readonly categorie = CATEGORIE;

  protected readonly prodotti = signal<Prodotto[]>([]);
  protected readonly eliminati = signal<Prodotto[]>([]);
  protected readonly caricamento = signal(false);
  protected readonly errore = signal('');

  protected readonly idInModifica = signal<number | null>(null);
  protected readonly idInEliminazione = signal<number | null>(null);

  protected readonly messaggio = signal('');
  protected readonly erroreAzione = signal('');
  protected readonly salvataggio = signal(false);

  protected readonly form: FormGroup = this.formBuilder.group({
    nome: ['', Validators.required],
    marca: ['', Validators.required],
    prezzo: [null, [Validators.required, Validators.min(0)]],
    immagineUrl: [''],
    categoria: ['', Validators.required],
    dettagli: [''],
  });

  // All'avvio carica prodotti ed eliminati
  ngOnInit(): void {
    this.carica();
    this.caricaEliminati();
  }

  /* Apre il form di modifica per un prodotto, precompilandolo. */
  modifica(prodotto: Prodotto): void {
    this.idInModifica.set(prodotto.id);
    this.idInEliminazione.set(null);
    this.erroreAzione.set('');
    this.messaggio.set('');

    // Precompila i campi (compresi i dettagli, testo libero).
    this.form.patchValue({
      nome: prodotto.nome,
      marca: prodotto.marca ?? '',
      prezzo: prodotto.prezzo,
      immagineUrl: prodotto.immagineUrl ?? '',
      categoria: prodotto.categoria,
      dettagli: prodotto.dettagli ?? '',
    });
  }

  /* Chiude il form di modifica senza salvare. */
  annullaModifica(): void {
    this.idInModifica.set(null);
  }

  /* Salva le modifiche */
  salva(): void {
    const id = this.idInModifica();
    if (id === null) {
      return;
    }
    if (this.form.invalid) {
      this.erroreAzione.set('Controlla i campi obbligatori (nome, marca, prezzo, categoria).');
      return;
    }

    this.salvataggio.set(true);
    this.erroreAzione.set('');

    const v = this.form.getRawValue();

    const prodotto: NuovoProdotto = {
      nome: v.nome,
      categoria: v.categoria,
      marca: this.testoOppureNull(v.marca),
      prezzo: v.prezzo,
      quantita: 0, // ignorata dal backend: la giacenza cambia solo con ordini e rifornimenti
      immagineUrl: this.testoOppureNull(v.immagineUrl),
      dettagli: this.testoOppureNull(v.dettagli),
    };

    this.prodottoService.aggiorna(id, prodotto).subscribe({
      next: () => {
        this.salvataggio.set(false);
        this.idInModifica.set(null);
        this.messaggio.set('Prodotto aggiornato.');
        this.carica();
      },
      error: (risposta) => {
        this.salvataggio.set(false);
        this.erroreAzione.set(risposta?.error?.messaggio ?? 'Impossibile salvare le modifiche.');
      },
    });
  }

  /* Chiede conferma prima di eliminare un prodotto. */
  chiediElimina(id: number): void {
    this.idInEliminazione.set(id);
    this.idInModifica.set(null);
    this.erroreAzione.set('');
    this.messaggio.set('');
  }

  /* Annulla la richiesta di eliminazione. */
  annullaElimina(): void {
    this.idInEliminazione.set(null);
  }

  /* Elimina il prodotto dal catalogo */
  confermaElimina(id: number): void {
    this.erroreAzione.set('');
    this.prodottoService.elimina(id).subscribe({
      next: () => {
        this.idInEliminazione.set(null);
        this.messaggio.set('Prodotto eliminato.');
        this.carica();
        this.caricaEliminati();
      },
      error: (risposta) => {
        this.erroreAzione.set(risposta?.error?.messaggio ?? 'Impossibile eliminare il prodotto.');
      },
    });
  }

  /* Rimette in vendita un prodotto eliminato */
  ripristina(prodotto: Prodotto): void {
    this.messaggio.set('');
    this.erroreAzione.set('');
    this.prodottoService.ripristina(prodotto.id).subscribe({
      next: () => {
        this.messaggio.set(`"${prodotto.nome}" è di nuovo in vendita.`);
        this.carica();
        this.caricaEliminati();
      },
      error: (risposta) => {
        this.erroreAzione.set(risposta?.error?.messaggio ?? 'Impossibile rimettere in vendita il prodotto.');
      },
    });
  }

  /* Scarica l'elenco dei prodotti eliminati */
  private caricaEliminati(): void {
    this.prodottoService.elencaEliminati().subscribe({
      next: (elenco) => this.eliminati.set(elenco),
      error: () => this.eliminati.set([]),
    });
  }

  /* Restituisce il testo ripulito, oppure null se è vuoto. */
  private testoOppureNull(testo: string | null): string | null {
    const pulito = (testo ?? '').trim();
    return pulito === '' ? null : pulito;
  }

  /* Scarica l'elenco di tutti i prodotti. */
  private carica(): void {
    this.caricamento.set(true);
    this.errore.set('');
    this.prodottoService.elencaTutti().subscribe({
      next: (elenco) => {
        this.prodotti.set(elenco);
        this.caricamento.set(false);
      },
      error: () => {
        this.prodotti.set([]);
        this.errore.set('Impossibile caricare i prodotti. Verifica che il backend sia in esecuzione.');
        this.caricamento.set(false);
      },
    });
  }
}
