import { Component, input } from '@angular/core';

@Component({
  selector: 'app-icon',
  standalone: true,
  template: `
    <span
      class="material-symbols-rounded app-icon"
      [style.font-size.px]="size()"
      aria-hidden="true"
    >{{ name() }}</span>
  `,
  styles: [`
    :host { display: inline-flex; align-items: center; justify-content: center; line-height: 1; }
    .app-icon { display: inline-block; line-height: 1; user-select: none; }
  `],
})
export class IconComponent {
  readonly name = input.required<string>();
  readonly size = input(24);
}
