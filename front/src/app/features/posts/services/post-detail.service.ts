import { Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { PostDetail } from '../../../core/api/posts.models';
import { PostsService } from '../../../core/api/posts.service';

@Injectable()
export class PostDetailService {
  private readonly _post = signal<PostDetail | null>(null);
  private readonly _loading = signal(true);
  private readonly _submitting = signal(false);
  private readonly _errorMessage = signal<string | null>(null);

  readonly post = this._post.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly submitting = this._submitting.asReadonly();
  readonly errorMessage = this._errorMessage.asReadonly();

  constructor(private readonly postsService: PostsService) {}

  loadPost(postId: number): void {
    this._loading.set(true);
    this._errorMessage.set(null);

    this.postsService
      .getPost(postId)
      .pipe(
        finalize(() => {
          this._loading.set(false);
        })
      )
      .subscribe({
        next: (post) => {
          this._post.set(post);
        },
        error: () => {
          this._errorMessage.set('Impossible de charger cet article.');
        },
      });
  }

  submitComment(content: string): void {
    const post = this._post();
    if (!post || this._submitting()) {
      return;
    }

    this._submitting.set(true);
    this.postsService
      .createComment(post.id, { content })
      .pipe(
        finalize(() => {
          this._submitting.set(false);
        })
      )
      .subscribe({
        next: (comment) => {
          const current = this._post();
          if (!current) {
            return;
          }

          this._post.set({
            ...current,
            comments: [...current.comments, comment],
          });
        },
        error: () => {
          this._errorMessage.set("Impossible d'envoyer le commentaire.");
        },
      });
  }

  setInvalidPostError(): void {
    this._errorMessage.set('Article introuvable.');
    this._loading.set(false);
  }
}
