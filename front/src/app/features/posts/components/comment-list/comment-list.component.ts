import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { PostComment } from '../../../../core/api/posts.models';

@Component({
  selector: 'app-comment-list',
  standalone: true,
  templateUrl: './comment-list.component.html',
  styleUrls: ['./comment-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommentListComponent {
  readonly comments = input.required<PostComment[]>();
}
