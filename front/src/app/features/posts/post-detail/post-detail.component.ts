import { CommonModule, DatePipe } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { PostDetail } from '../../../core/api/posts.models';
import { PostsService } from '../../../core/api/posts.service';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink, ReactiveFormsModule, MatIconModule],
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.scss'],
})
export class PostDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly postsService = inject(PostsService);
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  post: PostDetail | null = null;
  loading = true;
  submitting = false;
  errorMessage: string | null = null;

  readonly commentForm = this.fb.group({
    content: ['', [Validators.required, Validators.maxLength(2000)]],
  });

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const postId = Number(params.get('id'));
      if (Number.isNaN(postId)) {
        this.errorMessage = 'Article introuvable.';
        this.loading = false;
        return;
      }
      this.loadPost(postId);
    });
  }

  submitComment(): void {
    if (!this.post || this.commentForm.invalid || this.submitting) {
      this.commentForm.markAllAsTouched();
      return;
    }

    const content = this.commentForm.controls.content.value?.trim();
    if (!content) {
      this.commentForm.controls.content.setErrors({ required: true });
      return;
    }

    this.submitting = true;
    this.postsService
      .createComment(this.post.id, { content })
      .pipe(
        finalize(() => {
          this.submitting = false;
        })
      )
      .subscribe({
        next: (comment) => {
          if (!this.post) {
            return;
          }
          this.post = {
            ...this.post,
            comments: [...this.post.comments, comment],
          };
          this.commentForm.reset();
        },
        error: () => {
          this.errorMessage = "Impossible d'envoyer le commentaire.";
        },
      });
  }

  private loadPost(postId: number): void {
    this.loading = true;
    this.errorMessage = null;

    this.postsService
      .getPost(postId)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (post) => {
          this.post = post;
        },
        error: () => {
          this.errorMessage = 'Impossible de charger cet article.';
        },
      });
  }
}
