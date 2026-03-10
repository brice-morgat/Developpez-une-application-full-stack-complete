import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { TopicViewModel } from '../../services/topic-subscription.service';

@Component({
  selector: 'app-topic-card',
  standalone: true,
  imports: [MatButtonModule],
  templateUrl: './topic-card.component.html',
  styleUrls: ['./topic-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TopicCardComponent {
  readonly topic = input.required<TopicViewModel>();
  readonly pending = input(false);
  readonly toggle = output<TopicViewModel>();

  protected onToggle(): void {
    this.toggle.emit(this.topic());
  }
}
