import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, effect, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { SubscriptionCardComponent } from './components/subscription-card/subscription-card.component';
import {
  ProfileSettingsService,
  SaveProfilePayload,
  SubscriptionViewModel,
} from './services/profile-settings.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  providers: [ProfileSettingsService],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    SubscriptionCardComponent,
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProfileComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly profileSettingsService = inject(ProfileSettingsService);

  protected readonly form = this.fb.nonNullable.group({
    username: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.minLength(6)]],
  });

  protected readonly subscriptions = this.profileSettingsService.subscriptions;
  protected readonly loading = this.profileSettingsService.loading;
  protected readonly saving = this.profileSettingsService.saving;
  protected readonly pendingUnsubscribeTopicId = this.profileSettingsService.pendingUnsubscribeTopicId;
  protected readonly errorMessage = this.profileSettingsService.errorMessage;
  protected readonly successMessage = this.profileSettingsService.successMessage;

  constructor() {
    effect(() => {
      const profile = this.profileSettingsService.profile();
      if (!profile) {
        return;
      }

      this.form.setValue({
        username: profile.username,
        email: profile.email,
        password: '',
      });
    });
  }

  ngOnInit(): void {
    this.profileSettingsService.loadProfileData();
  }

  protected save(): void {
    if (this.form.invalid || this.saving()) {
      this.form.markAllAsTouched();
      return;
    }

    const payload: SaveProfilePayload = {
      username: this.form.controls.username.value.trim(),
      email: this.form.controls.email.value.trim(),
      password: this.form.controls.password.value.trim() || undefined,
    };

    this.profileSettingsService.saveProfile(payload);
  }

  protected unsubscribe(topic: SubscriptionViewModel): void {
    this.profileSettingsService.unsubscribe(topic);
  }
}
