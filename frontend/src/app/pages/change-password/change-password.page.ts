import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { ApiError } from '../../models/auth.models';
import { ButtonComponent } from '../../shared/ui/button/button.component';
import { FormFieldComponent } from '../../shared/ui/form-field/form-field.component';

@Component({
  selector: 'app-change-password-page',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonComponent, FormFieldComponent],
  template: `
    <section class="change-page">
      <div>
        <p class="eyebrow">Seguridad</p>
        <h1>Cambiar contraseña</h1>
        <p class="change-copy">Confirma tu contraseña actual y define una nueva.</p>
      </div>
      <form class="change-card" [formGroup]="form" (ngSubmit)="submit()" novalidate>
        <app-form-field fieldId="current-password" label="Contraseña actual" type="password" autocomplete="current-password" [control]="form.controls.currentPassword" [error]="errorFor('currentPassword')" />
        <app-form-field fieldId="new-password" label="Nueva contraseña" type="password" autocomplete="new-password" placeholder="Mínimo 8 caracteres" [control]="form.controls.newPassword" [error]="errorFor('newPassword')" />
        <app-form-field fieldId="confirm-new-password" label="Confirmar nueva contraseña" type="password" autocomplete="new-password" [control]="form.controls.confirmPassword" [error]="errorFor('confirmPassword')" />
        @if (serverError()) { <div class="form-alert" role="alert">{{ serverError() }}</div> }
        <app-button type="submit" [loading]="submitting()">Actualizar contraseña</app-button>
      </form>
    </section>
  `,
  styles: [`
    .change-page { display: grid; gap: 1.4rem; max-width: 520px; }
    h1 { margin: .15rem 0 0; font-size: clamp(1.8rem, 8vw, 2.5rem); }
    .eyebrow { margin: 0; color: var(--color-primary); font-weight: 750; font-size: .85rem; text-transform: uppercase; letter-spacing: .08em; }
    .change-copy { margin: .6rem 0 0; color: var(--color-text-muted); }
    .change-card { display: grid; gap: 1rem; padding: 1.25rem; border: 1px solid var(--color-border); border-radius: var(--radius-xl); background: var(--color-surface); box-shadow: var(--shadow-card); }
    .form-alert { border-radius: var(--radius-md); padding: .75rem; color: var(--color-danger); background: var(--color-danger-soft); font-size: .9rem; }
  `],
})
export class ChangePasswordPage {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly submitting = signal(false);
  readonly serverError = signal<string | null>(null);

  readonly form = new FormGroup({
    currentPassword: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(8)] }),
    newPassword: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(8)] }),
    confirmPassword: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  submit(): void {
    this.serverError.set(null);
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    if (this.form.controls.newPassword.value !== this.form.controls.confirmPassword.value) {
      this.form.controls.confirmPassword.setErrors({ mismatch: true });
      this.form.controls.confirmPassword.markAsTouched();
      return;
    }

    this.submitting.set(true);
    this.auth.changePassword({
      currentPassword: this.form.controls.currentPassword.value,
      newPassword: this.form.controls.newPassword.value,
    }).pipe(finalize(() => this.submitting.set(false))).subscribe({
      next: () => {
        this.auth.logout();
        this.router.navigate(['/login'], { queryParams: { passwordChanged: '1' } });
      },
      error: (error: HttpErrorResponse) => this.serverError.set((error.error as ApiError)?.message || 'No se pudo cambiar la contraseña.'),
    });
  }

  errorFor(controlName: 'currentPassword' | 'newPassword' | 'confirmPassword'): string | null {
    const control = this.form.controls[controlName];
    if (!control.touched || !control.errors) return null;
    if (control.errors['required']) return 'Este campo es obligatorio.';
    if (control.errors['minlength']) return 'La contraseña debe tener al menos 8 caracteres.';
    if (control.errors['mismatch']) return 'Las contraseñas no coinciden.';
    return 'Revisa este campo.';
  }
}
