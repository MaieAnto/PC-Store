import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProdottoService } from '../../servizi/prodotto.service';
import { RifornimentoService } from '../../servizi/rifornimento.service';
import { Prodotto } from '../../models/prodotto';
import { RifornimentoRequest } from '../../models/rifornimento';

interface RigaBozza {
  idProdotto: number;
  nome: string;
  quantita: number;
  costoUnitario: number;
}

@Component({
  selector: 'app-rifornimento',
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
  templateUrl: './rifornimento.html',
  styleUrl: './rifornimento.css',
})
export class Rifornimento implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly prodottoService = inject(ProdottoService);
  private readonly rifornimentoService = inject(RifornimentoService);
  private readonly router = inject(Router);

  protected readonly prodotti = signal<Prodotto[]>([]);
  protected readonly caricamentoProdotti = signal(false);
  protected readonly erroreProdotti = signal('');
  protected readonly righe = signal<RigaBozza[]>([]);

  protected readonly tentato = signal(false);
  protected readonly erroreForm = signal('');
  protected readonly idInModifica = signal<number | null>(null);

  protected readonly formRiga: FormGroup = this.formBuilder.group({
    idProdotto: ['', Validators.required],
    quantita: [null, [Validators.required, Validators.min(1)]],
    costoUnitario: [null, [Validators.required, Validators.min(0)]],
  });

  protected readonly invio = signal(false);
  protected readonly erroreInvio = signal('');

  protected readonly totale = computed(() =>
    this.righe().reduce((somma, r) => somma + r.quantita * r.costoUnitario, 0)
  );

  // All'avvio carica l'elenco prodotti
  ngOnInit(): void {
    this.caricaProdotti();
  }

  // Aggiunge o aggiorna una riga della bozza di rifornimento
  salvaRiga(): void {
    this.tentato.set(true);
    this.erroreForm.set('');

    if (this.formRiga.invalid) {
      return;
    }

    const valori = this.formRiga.getRawValue();
    const id = Number(valori.idProdotto);
    const prodotto = this.prodotti().find((p) => p.id === id);
    if (!prodotto) {
      this.erroreForm.set('Scegli un prodotto.');
      return;
    }

    const quantita = Number(valori.quantita);
    if (!Number.isInteger(quantita)) {
      this.erroreForm.set('La quantità deve essere un numero intero.');
      return;
    }

    const duplicato = this.righe().some(
      (r) => r.idProdotto === id && r.idProdotto !== this.idInModifica()
    );
    if (duplicato) {
      this.erroreForm.set('Questo prodotto è già nella lista.');
      return;
    }

    const riga: RigaBozza = {
      idProdotto: id,
      nome: prodotto.nome,
      quantita,
      costoUnitario: Number(valori.costoUnitario),
    };

    if (this.idInModifica() !== null) {
      this.righe.set(this.righe().map((r) => (r.idProdotto === this.idInModifica() ? riga : r)));
    } else {
      this.righe.set([...this.righe(), riga]);
    }

    this.annulla();
  }

  // Carica una riga esistente nel form per modificarla
  modificaRiga(riga: RigaBozza): void {
    this.tentato.set(false);
    this.erroreForm.set('');
    this.idInModifica.set(riga.idProdotto);
    this.formRiga.setValue({
      idProdotto: riga.idProdotto + '',
      quantita: riga.quantita,
      costoUnitario: riga.costoUnitario,
    });
  }

  // Annulla l'inserimento/modifica della riga e resetta il form.
  annulla(): void {
    this.tentato.set(false);
    this.idInModifica.set(null);
    this.formRiga.reset({ idProdotto: '', quantita: null, costoUnitario: null });
  }

  // Rimuove una riga dalla bozza di rifornimento
  rimuoviRiga(idProdotto: number): void {
    this.righe.set(this.righe().filter((r) => r.idProdotto !== idProdotto));
    // Se stavo modificando proprio quella riga, esco dalla modalità modifica.
    if (this.idInModifica() === idProdotto) {
      this.annulla();
    }
  }

  // Invia al backend l'intero rifornimento e torna allo storico.
  registra(): void {
    if (this.righe().length === 0) {
      this.erroreInvio.set('Aggiungi almeno una riga al rifornimento.');
      return;
    }

    this.invio.set(true);
    this.erroreInvio.set('');

    const richiesta: RifornimentoRequest = {
      righe: this.righe().map((r) => ({
        idProdotto: r.idProdotto,
        quantita: r.quantita,
        costoUnitario: r.costoUnitario,
      })),
    };

    this.rifornimentoService.creaRifornimento(richiesta).subscribe({
      next: () => {
        this.invio.set(false);
        this.router.navigate(['/rifornimenti']);
      },
      error: (risposta) => {
        this.invio.set(false);
        this.erroreInvio.set(
          risposta?.error?.messaggio ?? 'Impossibile registrare il rifornimento. Riprova.'
        );
      },
    });
  }

  // Scarica l'elenco dei prodotti selezionabili per il rifornimento.
  private caricaProdotti(): void {
    this.caricamentoProdotti.set(true);
    this.erroreProdotti.set('');
    this.prodottoService.elencaTutti().subscribe({
      next: (elenco) => {
        this.prodotti.set(elenco);
        this.caricamentoProdotti.set(false);
      },
      error: () => {
        this.prodotti.set([]);
        this.erroreProdotti.set('Impossibile caricare i prodotti. Verifica che il backend sia in esecuzione.');
        this.caricamentoProdotti.set(false);
      },
    });
  }
}
