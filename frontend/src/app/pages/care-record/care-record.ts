import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { CareRecordService } from '../../core/care/care-record';
import { CreateCareRecordRequest } from '../../models/care-record.model';
import { ButtonComponent } from '../../shared/ui/button/button.component';
import { FormFieldComponent } from '../../shared/ui/form-field/form-field.component';
import { IconComponent } from '../../shared/ui/icon/icon.component';

@Component({
  selector: 'app-care-record',
  imports: [
    ReactiveFormsModule,
    ButtonComponent,
    FormFieldComponent,
    IconComponent,
  ],
  templateUrl: './care-record.html',
  styleUrl: './care-record.css',
})
export class CareRecord {
  private readonly fb = inject(FormBuilder);
  private readonly careRecordService = inject(CareRecordService);

  readonly loading = signal(false);
  readonly successMessage = signal('');
  readonly errorMessage = signal('');

  readonly form = this.fb.nonNullable.group({
    petName: ['', Validators.required],
    care: ['', Validators.required],
    animalType: ['', Validators.required],
    careDate: ['', Validators.required],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');

    const request: CreateCareRecordRequest = this.form.getRawValue();

    this.careRecordService
      .create(request)
      .pipe(
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: () => {
          this.successMessage.set('Cuidado registrado correctamente');
          this.form.reset();
        },
        error: () => {
          this.errorMessage.set('No se pudo registrar el cuidado');
        },
      });
  }

  petNameError(): string | null {
    const control = this.form.controls.petName;

    if (control.touched && control.hasError('required')) {
      return 'Ingresa el nombre de la mascota';
    }

    return null;
  }

  careError(): string | null {
    const control = this.form.controls.care;

    if (control.touched && control.hasError('required')) {
      return 'Ingresa el cuidado realizado';
    }

    return null;
  }

  animalTypeError(): string | null {
    const control = this.form.controls.animalType;

    if (control.touched && control.hasError('required')) {
      return 'Ingresa el tipo de animal';
    }

    return null;
  }

  careDateError(): string | null {
    const control = this.form.controls.careDate;

    if (control.touched && control.hasError('required')) {
      return 'Selecciona una fecha';
    }

    return null;
  }
}