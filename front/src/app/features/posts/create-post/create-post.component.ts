import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { PostsService } from '../../../core/api/posts.service';
import { TopicResponse } from '../../../core/api/topics.models';
import { TopicsService } from '../../../core/api/topics.service';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
  ],
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.scss'],
})
export class CreatePostComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly topicsService = inject(TopicsService);
  private readonly postsService = inject(PostsService);
  private readonly router = inject(Router);

  topics: TopicResponse[] = [];
  loadingTopics = true;
  submitting = false;
  errorMessage: string | null = null;

  readonly form = this.fb.group({
    topicId: [null as number | null, [Validators.required]],
    title: ['', [Validators.required, Validators.maxLength(200)]],
    content: ['', [Validators.required, Validators.maxLength(5000)]],
  });

  ngOnInit(): void {
    this.topicsService
      .getTopics()
      .pipe(
        finalize(() => {
          this.loadingTopics = false;
        })
      )
      .subscribe({
        next: (topics) => {
          this.topics = topics;
        },
        error: () => {
          this.errorMessage = 'Impossible de charger la liste des thèmes.';
        },
      });
  }

  submit(): void {
    if (this.form.invalid || this.submitting) {
      this.form.markAllAsTouched();
      return;
    }

    const topicId = this.form.controls.topicId.value;
    const title = this.form.controls.title.value?.trim() ?? '';
    const content = this.form.controls.content.value?.trim() ?? '';

    if (!topicId || !title || !content) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = null;
    this.submitting = true;

    this.postsService
      .createPost({ topicId, title, content })
      .pipe(
        finalize(() => {
          this.submitting = false;
        })
      )
      .subscribe({
        next: (post) => {
          this.router.navigate(['/post', post.id]);
        },
        error: () => {
          this.errorMessage = "Impossible de créer l'article.";
        },
      });
  }
}
