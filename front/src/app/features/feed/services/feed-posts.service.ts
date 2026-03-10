import { Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { FeedPost } from '../../../core/api/posts.models';
import { PostsService } from '../../../core/api/posts.service';

@Injectable()
export class FeedPostsService {
  private readonly _posts = signal<FeedPost[]>([]);
  private readonly _sort = signal<'asc' | 'desc'>('desc');
  private readonly _loading = signal(true);
  private readonly _errorMessage = signal<string | null>(null);

  readonly posts = this._posts.asReadonly();
  readonly sort = this._sort.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly errorMessage = this._errorMessage.asReadonly();

  constructor(private readonly postsService: PostsService) {}

  loadPosts(): void {
    this._loading.set(true);
    this._errorMessage.set(null);

    this.postsService
      .getFeed(this._sort())
      .pipe(
        finalize(() => {
          this._loading.set(false);
        })
      )
      .subscribe({
        next: (posts) => {
          this._posts.set(posts);
        },
        error: () => {
          this._errorMessage.set("Impossible de charger les articles.");
        },
      });
  }

  toggleSort(): void {
    this._sort.set(this._sort() === 'desc' ? 'asc' : 'desc');
    this.loadPosts();
  }
}
