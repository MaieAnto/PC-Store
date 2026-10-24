import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CATEGORIE, Categoria, NuovoProdotto } from '../../modelli/prodotto';
import { ProdottoService } from '../../servizi/prodotto.service';

@Component({
  selector: 'app-aggiungi-prodotto',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './aggiungi-prodotto.html',
  styleUrl: './aggiungi-prodotto.css',
})
export class AggiungiProdotto {
  private readonly formBuilder = inject(FormBuilder);
  private readonly prodottoService = inject(ProdottoService);

  protected readonly categorie: Categoria[] = CATEGORIE.filter((c) => c.endpoint !== null);
  private readonly primoEndpoint = this.categorie[0].endpoint as string;

  protected readonly inviato = signal(false);       // true dopo il primo tentativo di invio
  protected readonly caricamento = signal(false);   // true mentre aspettiamo il backend
  protected readonly errore = signal('');           // messaggio di errore ('' = nessuno)
  protected readonly messaggioOk = signal('');      // messaggio di successo ('' = nessuno)

  protected readonly form: FormGroup = this.formBuilder.group({
    categoria: [this.primoEndpoint, Validators.required],
    nome: ['', Validators.required],
    marca: ['', Validators.required],
    prezzo: [null, [Validators.required, Validators.min(0)]],
    quantita: [null, [Validators.required, Validators.min(0)]],
    immagineUrl: [''],
    dettagli: [''],
  });

  cambiaCategoria(endpoint: string): void {
    this.form.controls['categoria'].setValue(endpoint);
    this.messaggioOk.set('');
  }

  invia(): void {
    this.inviato.set(true);
    this.messaggioOk.set('');

    if (this.form.invalid) {
      return;
    }

    this.errore.set('');
    this.caricamento.set(true);

    const v = this.form.getRawValue();
    const endpoint: string = v.categoria;

    const categoriaScelta = this.categorie.find((c) => c.endpoint === endpoint);
    const etichettaCategoria = categoriaScelta ? categoriaScelta.etichetta : '';

    const prodotto: NuovoProdotto = {
      nome: v.nome,
      categoria: etichettaCategoria,
      marca: this.testoOppureNull(v.marca),
      prezzo: v.prezzo,
      quantita: v.quantita,
      immagineUrl: this.testoOppureNull(v.immagineUrl),
      dettagli: this.testoOppureNull(v.dettagli),
    };

    this.prodottoService.aggiungi(prodotto).subscribe({
      next: (creato) => {
        this.caricamento.set(false);
        this.inviato.set(false);
        this.messaggioOk.set(`Prodotto "${creato.nome}" aggiunto con successo.`);
        this.form.reset();
        this.form.controls['categoria'].setValue(endpoint);
      },
      error: (risposta) => {
        this.caricamento.set(false);
        this.errore.set(
          risposta?.error?.messaggio ??
            'Impossibile aggiungere il prodotto. Verifica che il backend sia in esecuzione.'
        );
      },
    });
  }

  private testoOppureNull(testo: string | null): string | null {
    const pulito = (testo ?? '').trim();
    return pulito === '' ? null : pulito;
  }
}
