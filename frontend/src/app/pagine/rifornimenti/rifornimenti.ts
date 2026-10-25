import { Component, OnInit, inject, signal } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RifornimentoService } from '../../servizi/rifornimento.service';
import { Rifornimento } from '../../models/rifornimento';

@Component({
  selector: 'app-rifornimenti',
  imports: [MatExpansionModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './rifornimenti.html',
  styleUrl: './rifornimenti.css',
})
export class Rifornimenti implements OnInit {
  private readonly rifornimentoService = inject(RifornimentoService);

  protected readonly rifornimenti = signal<Rifornimento[]>([]);
  protected readonly caricamento = signal(false);
  protected readonly errore = signal('');

  // All'avvio carica lo storico rifornimenti
  ngOnInit(): void {
    this.carica();
  }

  /* Costo totale del rifornimento */
  costoTotale(rifornimento: Rifornimento): number {
    return rifornimento.totaleCosto;
  }

  /* Numero di pezzi totali aggiunti */
  numeroPezzi(rifornimento: Rifornimento): number {
    return rifornimento.righe.reduce((somma, r) => somma + r.quantita, 0);
  }

  /* Data/ora del rifornimento */
  dataLeggibile(valore: string | number): string {
    const data = new Date(valore);
    return Number.isNaN(data.getTime()) ? '' : data.toLocaleString('it-IT');
  }

  // Scarica dal backend lo storico dei rifornimenti del fornitore.
  private carica(): void {
    this.caricamento.set(true);
    this.errore.set('');
    this.rifornimentoService.storicoRifornimenti().subscribe({
      next: (elenco) => {
        this.rifornimenti.set(elenco);
        this.caricamento.set(false);
      },
      error: () => {
        this.rifornimenti.set([]);
        this.errore.set('Impossibile caricare i rifornimenti. Verifica che il backend sia in esecuzione.');
        this.caricamento.set(false);
      },
    });
  }
}
