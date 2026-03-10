import { CommonModule, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommentFormComponent } from '../components/comment-form/comment-form.component';
import { CommentListComponent } from '../components/comment-list/comment-list.component';
import { PostDetailService } from '../services/post-detail.service';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  providers: [PostDetailService],
  imports: [CommonModule, DatePipe, RouterLink, MatIconModule, CommentListComponent, CommentFormComponent],
  templateUrl: './post-detail.component.html',
  styleUrls: ['./post-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);
  private readonly postDetailService = inject(PostDetailService);

  protected readonly post = this.postDetailService.post;
  protected readonly loading = this.postDetailService.loading;
  protected readonly submitting = this.postDetailService.submitting;
  protected readonly errorMessage = this.postDetailService.errorMessage;

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const postId = Number(params.get('id'));
      if (Number.isNaN(postId)) {
        this.postDetailService.setInvalidPostError();
        return;
      }

      this.postDetailService.loadPost(postId);
    });
  }

  protected submitComment(content: string): void {
    this.postDetailService.submitComment(content);
  }
}
