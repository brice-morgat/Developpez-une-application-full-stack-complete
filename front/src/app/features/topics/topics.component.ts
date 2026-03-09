import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { finalize } from 'rxjs';
import { TopicResponse } from '../../core/api/topics.models';
import { TopicsService } from '../../core/api/topics.service';

interface TopicViewModel extends TopicResponse {
  description: string;
}

@Component({
  selector: 'app-topics',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './topics.component.html',
  styleUrls: ['./topics.component.scss'],
})
export class TopicsComponent implements OnInit {
  private readonly topicsService = inject(TopicsService);

  topics: TopicViewModel[] = [];
  loading = true;
  errorMessage: string | null = null;
  pendingTopicId: number | null = null;

  ngOnInit(): void {
    this.loadTopics();
  }

  toggleSubscription(topic: TopicViewModel): void {
    if (this.pendingTopicId !== null) {
      return;
    }

    this.pendingTopicId = topic.id;
    this.errorMessage = null;

    const request$ = topic.subscribed
      ? this.topicsService.unsubscribe(topic.id)
      : this.topicsService.subscribe(topic.id);

    request$
      .pipe(
        finalize(() => {
          this.pendingTopicId = null;
        })
      )
      .subscribe({
        next: () => {
          topic.subscribed = !topic.subscribed;
        },
        error: () => {
          this.errorMessage = "Impossible de mettre à jour l'abonnement.";
        },
      });
  }

  private loadTopics(): void {
    this.loading = true;
    this.errorMessage = null;

    this.topicsService
      .getTopics()
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (topics) => {
          this.topics = topics.map((topic) => ({
            ...topic,
            description: `Retrouvez les derniers articles autour de ${topic.name}.`,
          }));
        },
        error: () => {
          this.errorMessage = 'Impossible de charger les thèmes.';
        },
      });
  }
}
