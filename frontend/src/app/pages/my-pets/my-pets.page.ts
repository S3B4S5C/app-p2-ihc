import { Component } from '@angular/core';
import { IconComponent } from '../../shared/ui/icon/icon.component';

@Component({
  selector: 'app-my-pets-page',
  standalone: true,
  imports: [IconComponent],
  template: `
    <section class="pets-page">
      <div class="pets-page__heading">
        <p class="eyebrow">Tu espacio</p>
        <h1>Mis Mascotas</h1>
      </div>
      <div class="pets-placeholder" aria-label="Área reservada para el listado de mascotas">
        <app-icon name="pets" [size]="44" />
        <p>Aquí aparecerán tus mascotas.</p>
      </div>
    </section>
  `,
  styles: [`
    .pets-page { display: grid; gap: 1.4rem; }
    .pets-page__heading h1 { margin: .15rem 0 0; font-size: clamp(1.8rem, 8vw, 2.5rem); }
    .eyebrow { margin: 0; color: var(--color-primary); font-weight: 750; font-size: .85rem; text-transform: uppercase; letter-spacing: .08em; }
    .pets-placeholder { min-height: 260px; border: 1px dashed var(--color-border-strong); border-radius: var(--radius-xl); background: var(--color-surface); display: grid; place-items: center; align-content: center; gap: .5rem; text-align: center; color: var(--color-text-muted); padding: 1.5rem; }
    .pets-placeholder p { margin: 0; }
  `],
})
export class MyPetsPage {}
