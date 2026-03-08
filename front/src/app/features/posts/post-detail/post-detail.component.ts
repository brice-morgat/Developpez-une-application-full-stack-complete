import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="page">
      <h1>Post Detail</h1>
      <article class="post-item">
        <h2>Sample Post Title</h2>
        <p>Sample post content ready to be replaced by API data.</p>
      </article>
      <h3>Comments</h3>
      <ul>
        <li *ngFor="let comment of comments">{{ comment }}</li>
      </ul>
    </section>
  `,
})
export class PostDetailComponent {
  comments = ['Great post', 'Very clear setup'];
}

