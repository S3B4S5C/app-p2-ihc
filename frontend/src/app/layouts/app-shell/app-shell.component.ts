import { Component, HostListener, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ThemeToggleComponent } from '../../shared/ui/theme-toggle/theme-toggle.component';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ThemeToggleComponent],
  templateUrl: './app-shell.component.html',
  styleUrl: './app-shell.component.css',
})
export class AppShellComponent {
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly menuOpen = signal(false);

  toggleMenu(event: MouseEvent): void { event.stopPropagation(); this.menuOpen.update((open) => !open); }
  closeMenu(): void { this.menuOpen.set(false); }

  logout(): void {
    this.auth.logout();
    this.closeMenu();
    this.router.navigateByUrl('/login');
  }

  @HostListener('document:click')
  onDocumentClick(): void { this.closeMenu(); }
}
