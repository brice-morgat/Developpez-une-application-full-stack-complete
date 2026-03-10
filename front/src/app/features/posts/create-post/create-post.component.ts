import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Router, RouterLink } from '@angular/router';
import { CreatePostPayloadView, PostCreationService } from '../services/post-creation.service';

@Component({
  selector: 'app-create-post',
  standalone: true,
  providers: [PostCreationService],
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
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreatePostComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly postCreationService = inject(PostCreationService);

  protected readonly topics = this.postCreationService.topics;
  protected readonly loadingTopics = this.postCreationService.loadingTopics;
  protected readonly submitting = this.postCreationService.submitting;
  protected readonly errorMessage = this.postCreationService.errorMessage;

  protected readonly form = this.fb.group({
    topicId: this.fb.control<number | null>(null, [Validators.required]),
    title: this.fb.nonNullable.control('', [Validators.required, Validators.maxLength(200)]),
    content: this.fb.nonNullable.control('', [Validators.required, Validators.maxLength(5000)]),
  });

  ngOnInit(): void {
    this.postCreationService.loadTopics();
  }

  protected submit(): void {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    const topicId = this.form.controls.topicId.value;
    const title = this.form.controls.title.value.trim();
    const content = this.form.controls.content.value.trim();

    if (!topicId || !title || !content) {
      this.form.markAllAsTouched();
      return;
    }

    const payload: CreatePostPayloadView = { topicId, title, content };
    this.postCreationService.createPost(payload, (post) => {
      void this.router.navigate(['/post', post.id]);
    });
  }
}
