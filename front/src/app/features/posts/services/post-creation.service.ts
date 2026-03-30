import { Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { FeedPost } from '../../../core/api/posts.models';
import { PostsService } from '../../../core/api/posts.service';
import { TopicResponse } from '../../../core/api/topics.models';
import { TopicsService } from '../../../core/api/topics.service';

export interface CreatePostPayloadView {
  topicId: number;
  title: string;
  content: string;
}

@Injectable()
export class PostCreationService {
  private readonly _topics = signal<TopicResponse[]>([]);
  private readonly _loadingTopics = signal(true);
  private readonly _submitting = signal(false);
  private readonly _errorMessage = signal<string | null>(null);

  readonly topics = this._topics.asReadonly();
  readonly loadingTopics = this._loadingTopics.asReadonly();
  readonly submitting = this._submitting.asReadonly();
  readonly errorMessage = this._errorMessage.asReadonly();

  constructor(
    private readonly topicsService: TopicsService,
    private readonly postsService: PostsService
  ) {}

  loadTopics(): void {
    this.topicsService
      .getTopics()
      .pipe(
        finalize(() => {
          this._loadingTopics.set(false);
        })
      )
      .subscribe({
        next: (topics) => {
          this._topics.set(topics);
        },
        error: () => {
          this._errorMessage.set('Impossible de charger la liste des thèmes.');
        },
      });
  }

  createPost(payload: CreatePostPayloadView, onSuccess: (post: FeedPost) => void): void {
    if (this._submitting()) {
      return;
    }

    this._errorMessage.set(null);
    this._submitting.set(true);

    this.postsService
      .createPost(payload)
      .pipe(
        finalize(() => {
          this._submitting.set(false);
        })
      )
      .subscribe({
        next: (post) => {
          onSuccess(post);
        },
        error: () => {
          this._errorMessage.set("Impossible de créer l'article.");
        },
      });
  }
}

