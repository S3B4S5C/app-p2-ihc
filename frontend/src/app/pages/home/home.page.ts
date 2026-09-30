import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IconComponent } from '../../shared/ui/icon/icon.component';
import { ThemeToggleComponent } from '../../shared/ui/theme-toggle/theme-toggle.component';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [RouterLink, IconComponent, ThemeToggleComponent],
  template: `
    <main class="public-page">
      <div class="public-page__theme"><app-theme-toggle /></div>
      <section class="public-card" aria-labelledby="home-title">
        <div class="public-mark"><app-icon name="pets" [size]="32" /></div>
        <p class="eyebrow">Mascotas al Día</p>
        <h1 id="home-title">El cuidado de tus mascotas, en un solo lugar.</h1>
        <p class="public-copy">
          Organiza el espacio de tus mascotas y mantén su información disponible desde el móvil.
        </p>
        <div class="public-actions">
          <a class="public-action public-action--primary" routerLink="/login">Iniciar sesión</a>
          <a class="public-action" routerLink="/registro">Crear cuenta</a>
        </div>
        <p class="public-note">Esta página es pública y puede abrirse sin iniciar sesión.</p>
      </section>
    </main>
  `,
  styles: [`
    .public-page { min-height: 100dvh; display: grid; place-items: center; padding: 1.25rem; position: relative; background: var(--color-bg); }
    .public-page__theme { position: absolute; top: 1rem; right: 1rem; }
    .public-card { width: min(100%, 520px); padding: 2rem 1.5rem; border: 1px solid var(--color-border); border-radius: var(--radius-xl); background: var(--color-surface); box-shadow: var(--shadow-card); text-align: center; }
    .public-mark { width: 58px; height: 58px; margin: 0 auto 1rem; display: grid; place-items: center; border-radius: 18px; color: var(--color-primary); background: var(--color-primary-soft); }
    .eyebrow { margin: 0 0 .5rem; color: var(--color-primary); font-weight: 800; letter-spacing: .08em; text-transform: uppercase; font-size: .8rem; }
    h1 { margin: 0; font-size: clamp(2rem, 10vw, 3rem); line-height: 1.05; letter-spacing: -.04em; }
    .public-copy { margin: 1rem auto 0; max-width: 38ch; color: var(--color-text-muted); line-height: 1.6; }
    .public-actions { display: grid; gap: .75rem; margin-top: 1.6rem; }
    .public-action { min-height: 48px; display: grid; place-items: center; padding: .75rem 1rem; border: 1px solid var(--color-border-strong); border-radius: var(--radius-md); color: var(--color-primary); font-weight: 750; text-decoration: none; }
    .public-action--primary { border-color: var(--color-primary); background: var(--color-primary); color: var(--color-on-primary); }
    .public-note { margin: 1.25rem 0 0; color: var(--color-text-muted); font-size: .82rem; }
    @media (min-width: 700px) { .public-card { padding: 2.6rem; } .public-actions { grid-template-columns: 1fr 1fr; } }
  `],
})
export class HomePage {}
