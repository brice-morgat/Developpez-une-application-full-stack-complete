import { CommonModule, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FeedPost } from '../../../../core/api/posts.models';

@Component({
  selector: 'app-post-card',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink],
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostCardComponent {
  readonly post = input.required<FeedPost>();
}
