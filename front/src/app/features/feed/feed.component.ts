import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
})
export class FeedComponent {
  samplePosts = [
    { title: 'Spring Boot 3 setup tips', excerpt: 'A minimal setup for MVP backend.' },
    { title: 'Angular feature folders', excerpt: 'A clean base with core/shared/features.' },
  ];
}

