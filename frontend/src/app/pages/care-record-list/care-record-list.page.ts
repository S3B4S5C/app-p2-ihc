import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { CareRecordService } from '../../core/care/care-record';
import { CareRecord } from '../../models/care-record.model';
import { IconComponent } from '../../shared/ui/icon/icon.component';
import { CareRecordCardComponent } from './care-record-card/care-record-card.component';

@Component({
  selector: 'app-care-record-list-page',
  standalone: true,
  imports: [
    RouterLink,
    IconComponent,
    CareRecordCardComponent,
  ],
  templateUrl: './care-record-list.page.html',
  styleUrl: './care-record-list.page.css',
})
export class CareRecordListPage implements OnInit {
  private readonly careRecordService = inject(CareRecordService);

  readonly records = signal<CareRecord[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal('');
  readonly statusErrorMessage = signal('');
  readonly completingId = signal<string | null>(null);

  ngOnInit(): void {
    this.loadRecords();
  }

  loadRecords(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.careRecordService
      .findAll()
      .pipe(
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (records) => {
          this.records.set(records);
        },
        error: () => {
          this.errorMessage.set(
            'No se pudieron cargar los cuidados',
          );
        },
      });
  }

  completeRecord(id: string): void {
    if (this.completingId() !== null) {
      return;
    }

    this.completingId.set(id);
    this.statusErrorMessage.set('');

    this.careRecordService
      .complete(id)
      .pipe(
        finalize(() => this.completingId.set(null)),
      )
      .subscribe({
        next: (updated) => {
          this.records.update((records) =>
            records.map((record) => record.id === updated.id ? updated : record),
          );
        },
        error: () => {
          this.statusErrorMessage.set('No se pudo marcar el cuidado como realizado');
        },
      });
  }
}
