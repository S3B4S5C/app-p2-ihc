import { Component, inject } from '@angular/core';
import { ThemeService } from '../../../core/theme/theme.service';
import { IconComponent } from '../icon/icon.component';

@Component({
  selector: 'app-theme-toggle',
  standalone: true,
  imports: [IconComponent],
  template: `
    <button class="theme-toggle" type="button" (click)="theme.toggle()" [attr.aria-label]="label">
      @if (theme.theme() === 'dark') {
        <app-icon name="light_mode" [size]="22" />
      } @else {
        <app-icon name="dark_mode" [size]="22" />
      }
    </button>
  `,
  styleUrl: './theme-toggle.component.css',
})
export class ThemeToggleComponent {
  readonly theme = inject(ThemeService);
  get label(): string { return this.theme.theme() === 'dark' ? 'Usar tema claro' : 'Usar tema oscuro'; }
}
