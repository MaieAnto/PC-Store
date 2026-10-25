import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AutenticazioneService } from '../../servizi/autenticazione.service';
import { Ruolo } from '../../models/utente';

@Component({
  selector: 'app-registrazione',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './registrazione.html',
  styleUrl: './registrazione.css',
})
export class Registrazione {
  private readonly formBuilder = inject(FormBuilder);
  private readonly autenticazioneService = inject(AutenticazioneService);

  protected readonly errore = signal('');
  protected readonly caricamento = signal(false);
  protected readonly completata = signal(false);
  protected readonly inviato = signal(false);
  protected readonly nascondiPassword = signal(true);

  protected readonly form = this.formBuilder.nonNullable.group({
    nome: ['', [Validators.required]],
    cognome: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    ruolo: ['CLIENTE', [Validators.required]],
  });

  // Mostra/nasconde la password
  cambiaVisibilitaPassword(): void {
    this.nascondiPassword.update((nascosta) => !nascosta);
  }

  // Invia il form di registrazione al backend (gestisce validazione, caricamento ed errori).
  invia(): void {
    this.inviato.set(true);

    if (this.form.invalid) {
      return;
    }

    this.errore.set('');
    this.caricamento.set(true);

    const dati = this.form.getRawValue();

    this.autenticazioneService.registra({ ...dati, ruolo: dati.ruolo as Ruolo }).subscribe({
        next: () => {
          this.caricamento.set(false);
          this.completata.set(true);
        },
        error: (risposta) => {
          this.caricamento.set(false);
          this.errore.set(
            risposta?.error?.messaggio ?? 'Registrazione non riuscita. Controlla i dati e che il backend sia avviato.',
          );
        },
      });
  }
}
