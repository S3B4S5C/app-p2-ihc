import { Component, inject } from '@angular/core';
import { ThemeService } from '../../../core/theme/theme.service';

@Component({
  selector: 'app-theme-toggle',
  standalone: true,
  template: `
    <button class="theme-toggle" type="button" (click)="theme.toggle()" [attr.aria-label]="label">
      @if (theme.theme() === 'dark') { ☀️ } @else { 🌙 }
    </button>
  `,
  styleUrl: './theme-toggle.component.css',
})
export class ThemeToggleComponent {
  readonly theme = inject(ThemeService);
  get label(): string { return this.theme.theme() === 'dark' ? 'Usar tema claro' : 'Usar tema oscuro'; }
}
