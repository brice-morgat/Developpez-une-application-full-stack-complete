import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

interface TopicViewModel {
  id: number;
  name: string;
  description: string;
  subscribed: boolean;
}

@Component({
  selector: 'app-topics',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './topics.component.html',
  styleUrls: ['./topics.component.scss'],
})
export class TopicsComponent {
  topics: TopicViewModel[] = [
    {
      id: 1,
      name: 'Titre du thème',
      description:
        "Description : lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard...",
      subscribed: false,
    },
    {
      id: 2,
      name: 'Titre du thème',
      description:
        "Description : lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard...",
      subscribed: true,
    },
    {
      id: 3,
      name: 'Titre du thème',
      description:
        "Description : lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard...",
      subscribed: true,
    },
    {
      id: 4,
      name: 'Titre du thème',
      description:
        "Description : lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard...",
      subscribed: false,
    },
  ];

  toggleSubscription(topic: TopicViewModel): void {
    topic.subscribed = !topic.subscribed;
  }
}
