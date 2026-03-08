import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <section class="page-shell">
      <router-outlet></router-outlet>
    </section>
  `,
})
export class MainLayoutComponent {}
