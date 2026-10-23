import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { OrdineService } from '../../servizi/ordine.service';
import { AutenticazioneService } from '../../servizi/autenticazione.service';
import { Ordine } from '../../modelli/ordine';

@Component({
  selector: 'app-ordini',
  imports: [RouterLink, MatExpansionModule, MatIconModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './ordini.html',
  styleUrl: './ordini.css',
})
export class Ordini implements OnInit {
  private readonly ordineService = inject(OrdineService);
  private readonly autenticazioneService = inject(AutenticazioneService);
  private readonly router = inject(Router);

  protected readonly utente = this.autenticazioneService.utenteCorrente;

  protected readonly ordini = signal<Ordine[]>([]);   // storico scaricato dal backend
  protected readonly caricamento = signal(false);     // true mentre aspettiamo il backend
  protected readonly errore = signal('');             // messaggio di errore ('' = nessuno)

  ngOnInit(): void {
    const utente = this.utente();
    if (!utente) {
      this.router.navigate(['/login']);
      return;
    }
    this.carica();
  }

  totaleOrdine(ordine: Ordine): number {
    return ordine.dettagli.reduce((somma, r) => somma + r.prezzoUnitario * r.quantita, 0);
  }

  numeroArticoli(ordine: Ordine): number {
    return ordine.dettagli.reduce((somma, r) => somma + r.quantita, 0);
  }

  dataLeggibile(valore: string | number): string {
    const data = new Date(valore);
    return Number.isNaN(data.getTime()) ? '' : data.toLocaleString('it-IT');
  }

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
