import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { SubscriptionViewModel } from '../../services/profile-settings.service';

@Component({
  selector: 'app-subscription-card',
  standalone: true,
  templateUrl: './subscription-card.component.html',
  styleUrls: ['./subscription-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubscriptionCardComponent {
  readonly topic = input.required<SubscriptionViewModel>();
  readonly pending = input(false);
  readonly unsubscribe = output<SubscriptionViewModel>();

  protected onUnsubscribe(): void {
    this.unsubscribe.emit(this.topic());
  }
}
