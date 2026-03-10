import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { PostCardComponent } from './components/post-card/post-card.component';
import { FeedPostsService } from './services/feed-posts.service';

@Component({
  selector: 'app-feed',
  standalone: true,
  providers: [FeedPostsService],
  imports: [CommonModule, RouterLink, MatButtonModule, MatIconModule, PostCardComponent],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeedComponent implements OnInit {
  private readonly feedPostsService = inject(FeedPostsService);

  protected readonly posts = this.feedPostsService.posts;
  protected readonly sort = this.feedPostsService.sort;
  protected readonly loading = this.feedPostsService.loading;
  protected readonly errorMessage = this.feedPostsService.errorMessage;

  ngOnInit(): void {
    this.feedPostsService.loadPosts();
  }

  protected toggleSort(): void {
    this.feedPostsService.toggleSort();
  }
}
