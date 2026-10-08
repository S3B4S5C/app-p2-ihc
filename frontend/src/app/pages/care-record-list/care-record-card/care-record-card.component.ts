import { Component, input, output } from '@angular/core';

import { CareRecord } from '../../../models/care-record.model';
import { IconComponent } from '../../../shared/ui/icon/icon.component';

@Component({
  selector: 'app-care-record-card',
  standalone: true,
  imports: [IconComponent],
  templateUrl: './care-record-card.component.html',
  styleUrl: './care-record-card.component.css',
})
export class CareRecordCardComponent {
  readonly record = input.required<CareRecord>();
  readonly completing = input(false);
  readonly complete = output<string>();
}
