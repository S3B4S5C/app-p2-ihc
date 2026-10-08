import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { CareRecordService } from '../../core/care/care-record';
import { CareRecord } from '../../models/care-record.model';
import { IconComponent } from '../../shared/ui/icon/icon.component';

@Component({
  selector: 'app-care-record-list-page',
  standalone: true,
  imports: [
    RouterLink,
    IconComponent,
  ],
  templateUrl: './care-record-list.page.html',
  styleUrl: './care-record-list.page.css',
})
export class CareRecordListPage implements OnInit {
  private readonly careRecordService = inject(CareRecordService);

  readonly records = signal<CareRecord[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal('');

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
}