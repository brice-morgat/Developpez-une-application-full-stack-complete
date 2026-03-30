import { Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { TopicResponse } from '../../../core/api/topics.models';
import { TopicsService } from '../../../core/api/topics.service';

export interface TopicViewModel extends TopicResponse {
  description: string;
}

@Injectable()
export class TopicSubscriptionService {
  private readonly _topics = signal<TopicViewModel[]>([]);
  private readonly _loading = signal(true);
  private readonly _errorMessage = signal<string | null>(null);
  private readonly _pendingTopicId = signal<number | null>(null);

  readonly topics = this._topics.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly errorMessage = this._errorMessage.asReadonly();
  readonly pendingTopicId = this._pendingTopicId.asReadonly();

  constructor(private readonly topicsService: TopicsService) {}

  loadTopics(): void {
    this._loading.set(true);
    this._errorMessage.set(null);

    this.topicsService
      .getTopics()
      .pipe(
        finalize(() => {
          this._loading.set(false);
        })
      )
      .subscribe({
        next: (topics) => {
          this._topics.set(this.toTopicsViewModel(topics));
        },
        error: () => {
          this._errorMessage.set('Impossible de charger les thèmes.');
        },
      });
  }

  toggleSubscription(topic: TopicViewModel): void {
    if (this._pendingTopicId() !== null) {
      return;
    }

    this._pendingTopicId.set(topic.id);
    this._errorMessage.set(null);

    const request$ = topic.subscribed
      ? this.topicsService.unsubscribe(topic.id)
      : this.topicsService.subscribe(topic.id);

    request$
      .pipe(
        finalize(() => {
          this._pendingTopicId.set(null);
        })
      )
      .subscribe({
        next: () => {
          this._topics.set(
            this._topics().map((item) =>
              item.id === topic.id ? { ...item, subscribed: !item.subscribed } : item
            )
          );
        },
        error: () => {
          this._errorMessage.set("Impossible de mettre à jour l'abonnement.");
        },
      });
  }

  private toTopicsViewModel(topics: TopicResponse[]): TopicViewModel[] {
    return topics.map((topic) => ({
      ...topic,
      description: `Retrouvez les derniers articles autour de ${topic.name}.`,
    }));
  }
}

