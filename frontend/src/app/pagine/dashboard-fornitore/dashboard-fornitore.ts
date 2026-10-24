import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProdottoService } from '../../servizi/prodotto.service';
import { RifornimentoService } from '../../servizi/rifornimento.service';
import { AutenticazioneService } from '../../servizi/autenticazione.service';
import { Prodotto } from '../../modelli/prodotto';
import { Rifornimento } from '../../modelli/rifornimento';
import { Ruolo } from '../../modelli/utente';

@Component({
  selector: 'app-dashboard-fornitore',
  imports: [RouterLink, MatCardModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './dashboard-fornitore.html',
  styleUrl: './dashboard-fornitore.css',
})
export class DashboardFornitore implements OnInit {
  private readonly prodottoService = inject(ProdottoService);
  private readonly rifornimentoService = inject(RifornimentoService);
  private readonly auth = inject(AutenticazioneService);
  private readonly router = inject(Router);

  protected readonly utente = this.auth.utenteCorrente;

  protected readonly SOGLIA_SCORTA = 5;

  protected readonly prodotti = signal<Prodotto[]>([]);
  protected readonly eliminati = signal<Prodotto[]>([]);
  protected readonly rifornimenti = signal<Rifornimento[]>([]);
  protected readonly caricamento = signal(false);
  protected readonly errore = signal('');

  protected readonly numProdotti = computed(() => this.prodotti().length);
  protected readonly numEsauriti = computed(() => this.prodotti().filter((p) => p.quantita === 0).length);
  protected readonly numEliminati = computed(() => this.eliminati().length);
  protected readonly valoreMagazzino = computed(() =>
    this.prodotti().reduce((somma, p) => somma + p.prezzo * p.quantita, 0)
  );

  protected readonly daRifornire = computed(() =>
    this.prodotti()
      .filter((p) => p.quantita <= this.SOGLIA_SCORTA)
      .sort((a, b) => a.quantita - b.quantita)
  );

  protected readonly rifornimentiRecenti = computed(() => this.rifornimenti().slice(0, 5));

  ngOnInit(): void {
    const u = this.utente();
    if (!u || u.ruolo !== Ruolo.FORNITORE) {
      this.router.navigate(['/login']);
      return;
    }
    this.carica();
  }

  costoTotale(r: Rifornimento): number {
    return r.dettagli.reduce((somma, d) => somma + d.quantita * d.costoUnitario, 0);
  }
  numeroPezzi(r: Rifornimento): number {
    return r.dettagli.reduce((somma, d) => somma + d.quantita, 0);
  }
  dataLeggibile(valore: string | number): string {
    const d = new Date(valore);
    return Number.isNaN(d.getTime()) ? '' : d.toLocaleDateString('it-IT');
  }

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
        this.errore.set('Impossibile caricare i dati. Verifica che il backend sia in esecuzione.');
        this.caricamento.set(false);
      },
    });

    this.prodottoService.elencaEliminati().subscribe({
      next: (elenco) => this.eliminati.set(elenco),
      error: () => this.eliminati.set([]),
    });

    this.rifornimentoService.storicoRifornimenti().subscribe({
      next: (elenco) => this.rifornimenti.set(elenco),
      error: () => this.rifornimenti.set([]),
    });
  }
}
