import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="page">
      <h1>Feed</h1>
      <article *ngFor="let post of samplePosts" class="post-item">
        <h2>{{ post.title }}</h2>
        <p>{{ post.excerpt }}</p>
      </article>
    </section>
  `,
})
export class FeedComponent {
  samplePosts = [
    { title: 'Spring Boot 3 setup tips', excerpt: 'A minimal setup for MVP backend.' },
    { title: 'Angular feature folders', excerpt: 'A clean base with core/shared/features.' },
  ];
}

