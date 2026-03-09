import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { FeedPost } from '../../core/api/posts.models';
import { PostsService } from '../../core/api/posts.service';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, RouterLink, MatButtonModule, MatIconModule],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
})
export class FeedComponent implements OnInit {
  private readonly postsService = inject(PostsService);

  posts: FeedPost[] = [];
  sort: 'asc' | 'desc' = 'desc';
  loading = true;
  errorMessage: string | null = null;

  ngOnInit(): void {
    this.loadPosts();
  }

  toggleSort(): void {
    this.sort = this.sort === 'desc' ? 'asc' : 'desc';
    this.loadPosts();
  }

  private loadPosts(): void {
    this.loading = true;
    this.errorMessage = null;

    this.postsService
      .getFeed(this.sort)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (posts) => {
          this.posts = posts;
        },
        error: () => {
          this.errorMessage = "Impossible de charger les articles.";
        },
      });
  }
}
