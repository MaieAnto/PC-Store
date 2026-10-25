import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AutenticazioneService } from '../servizi/autenticazione.service';
import { CarrelloService } from '../servizi/carrello.service';
import { Ruolo } from '../models/utente';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatButtonModule, MatIconModule],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout implements OnInit {
  private readonly auth = inject(AutenticazioneService);
  private readonly carrelloService = inject(CarrelloService);
  private readonly router = inject(Router);

  protected readonly utente = this.auth.utenteCorrente;
  protected readonly eCliente = computed(() => this.utente()?.ruolo === Ruolo.CLIENTE);
  protected readonly eFornitore = computed(() => this.utente()?.ruolo === Ruolo.FORNITORE);
  protected readonly numeroCarrello = this.carrelloService.numeroArticoli;
  protected readonly menuAperto = signal(false);

  // All'avvio: se l'utente e' un cliente carica il carrello, e chiude il menu a ogni cambio pagina.
  ngOnInit(): void {
    const u = this.utente();
    if (u && u.ruolo === Ruolo.CLIENTE) {
      this.carrelloService.getDettagli().subscribe({ error: () => {} });
    }
    this.router.events
      .pipe(filter((e) => e instanceof NavigationEnd))
      .subscribe(() => this.menuAperto.set(false));
  }

  toggleMenu(): void {
    this.menuAperto.update((v) => !v);
  }

  chiudiMenu(): void {
    this.menuAperto.set(false);
  }

  logout(): void {
    this.carrelloService.azzeraConteggio();
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
