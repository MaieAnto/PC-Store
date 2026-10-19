import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AutenticazioneService } from '../../servizi/autenticazione.service';
import { Ruolo } from '../../modelli/utente';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  private readonly formBuilder = inject(FormBuilder);
  private readonly autenticazioneService = inject(AutenticazioneService);
  private readonly router = inject(Router);

  protected readonly errore = signal('');        // messaggio di errore ('' = nessuno)
  protected readonly caricamento = signal(false); // true mentre aspettiamo il backend
  protected readonly nascondiPassword = signal(true); // true = password oscurata (pallini)

  protected readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  // Mostra/nasconde la password
  cambiaVisibilitaPassword(): void {
    this.nascondiPassword.update((nascosta) => !nascosta);
  }

  // Metodo richiamato quando l'utente invia il form (bottone "Accedi")
  invia(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errore.set('');
    this.caricamento.set(true);

    const { email, password } = this.form.getRawValue();

    this.autenticazioneService.login(email, password).subscribe({
      next: (utente) => {
        this.caricamento.set(false);
        if (utente.ruolo === Ruolo.FORNITORE) {
          this.router.navigate(['/fornitore']);
        } else {
          this.router.navigate(['/catalogo']);
        }
      },
      error: (risposta) => {
        this.caricamento.set(false);
        this.errore.set(risposta?.error?.messaggio ?? 'Accesso non riuscito. Controlla le credenziali');
      },
    });
  }
}
