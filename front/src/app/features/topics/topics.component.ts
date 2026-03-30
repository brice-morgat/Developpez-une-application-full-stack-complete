import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-topics',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="page">
      <h1>Topics</h1>
      <ul>
        <li *ngFor="let topic of topics">{{ topic }}</li>
      </ul>
    </section>
  `,
})
export class TopicsComponent {
  topics = ['Java', 'Angular', 'Spring Security'];
}

