import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { TopicCardComponent } from './components/topic-card/topic-card.component';
import { TopicViewModel, TopicSubscriptionService } from './services/topic-subscription.service';

@Component({
  selector: 'app-topics',
  standalone: true,
  providers: [TopicSubscriptionService],
  imports: [CommonModule, TopicCardComponent],
  templateUrl: './topics.component.html',
  styleUrls: ['./topics.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TopicsComponent implements OnInit {
  private readonly topicSubscriptionService = inject(TopicSubscriptionService);

  protected readonly topics = this.topicSubscriptionService.topics;
  protected readonly loading = this.topicSubscriptionService.loading;
  protected readonly errorMessage = this.topicSubscriptionService.errorMessage;
  protected readonly pendingTopicId = this.topicSubscriptionService.pendingTopicId;

  ngOnInit(): void {
    this.topicSubscriptionService.loadTopics();
  }

  protected toggleSubscription(topic: TopicViewModel): void {
    this.topicSubscriptionService.toggleSubscription(topic);
  }
}
