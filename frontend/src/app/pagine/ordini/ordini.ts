import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { OrdineService } from '../../servizi/ordine.service';
import { Ordine } from '../../models/ordine';

@Component({
  selector: 'app-ordini',
  imports: [RouterLink, MatExpansionModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './ordini.html',
  styleUrl: './ordini.css',
})
export class Ordini implements OnInit {
  private readonly ordineService = inject(OrdineService);

  protected readonly ordini = signal<Ordine[]>([]);
  protected readonly caricamento = signal(false);
  protected readonly errore = signal('');

  // All'avvio carica lo storico ordini
  ngOnInit(): void {
    this.carica();
  }

  // Totale dell'ordine.
  totaleOrdine(ordine: Ordine): number {
    return ordine.totale;
  }

  // Numero totale di articoli in un ordine.
  numeroArticoli(ordine: Ordine): number {
    return ordine.righe.reduce((somma, r) => somma + r.quantita, 0);
  }

  dataLeggibile(valore: string | number): string {
    const data = new Date(valore);
    return Number.isNaN(data.getTime()) ? '' : data.toLocaleString('it-IT');
  }

  // Scarica dal backend lo storico ordini dell'utente.
  private carica(): void {
    this.caricamento.set(true);
    this.errore.set('');
    this.ordineService.storicoOrdini().subscribe({
      next: (elenco) => {
        this.ordini.set(elenco);
        this.caricamento.set(false);
      },
      error: () => {
        this.ordini.set([]);
        this.errore.set('Impossibile caricare gli ordini. Verifica che il backend sia in esecuzione.');
        this.caricamento.set(false);
      },
    });
  }
}
