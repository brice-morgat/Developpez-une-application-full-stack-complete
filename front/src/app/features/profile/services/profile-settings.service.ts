import { Injectable, signal } from '@angular/core';
import { finalize, forkJoin } from 'rxjs';
import { TopicResponse } from '../../../core/api/topics.models';
import { TopicsService } from '../../../core/api/topics.service';
import { UsersService } from '../../../core/api/users.service';

export interface SubscriptionViewModel {
  id: number;
  name: string;
  description: string;
}

export interface ProfileViewModel {
  username: string;
  email: string;
}

export interface SaveProfilePayload {
  username: string;
  email: string;
  password?: string;
}

@Injectable()
export class ProfileSettingsService {
  private readonly _profile = signal<ProfileViewModel | null>(null);
  private readonly _subscriptions = signal<SubscriptionViewModel[]>([]);
  private readonly _loading = signal(true);
  private readonly _saving = signal(false);
  private readonly _pendingUnsubscribeTopicId = signal<number | null>(null);
  private readonly _errorMessage = signal<string | null>(null);
  private readonly _successMessage = signal<string | null>(null);

  readonly profile = this._profile.asReadonly();
  readonly subscriptions = this._subscriptions.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly saving = this._saving.asReadonly();
  readonly pendingUnsubscribeTopicId = this._pendingUnsubscribeTopicId.asReadonly();
  readonly errorMessage = this._errorMessage.asReadonly();
  readonly successMessage = this._successMessage.asReadonly();

  constructor(
    private readonly usersService: UsersService,
    private readonly topicsService: TopicsService
  ) {}

  loadProfileData(): void {
    this._loading.set(true);
    this._errorMessage.set(null);

    forkJoin({
      user: this.usersService.me(),
      topics: this.topicsService.getTopics(),
    })
      .pipe(
        finalize(() => {
          this._loading.set(false);
        })
      )
      .subscribe({
        next: ({ user, topics }) => {
          this._profile.set({
            username: user.username,
            email: user.email,
          });
          this._subscriptions.set(this.toSubscriptions(topics));
        },
        error: () => {
          this._errorMessage.set('Impossible de charger le profil utilisateur.');
        },
      });
  }

  saveProfile(payload: SaveProfilePayload): void {
    if (this._saving()) {
      return;
    }

    this._saving.set(true);
    this._errorMessage.set(null);
    this._successMessage.set(null);

    this.usersService
      .updateMe(payload)
      .pipe(
        finalize(() => {
          this._saving.set(false);
        })
      )
      .subscribe({
        next: (user) => {
          this._profile.set({
            username: user.username,
            email: user.email,
          });
          this._successMessage.set('Profil mis à jour.');
        },
        error: (error) => {
          this._errorMessage.set(error?.error?.message ?? 'Impossible de mettre à jour le profil.');
        },
      });
  }

  unsubscribe(topic: SubscriptionViewModel): void {
    if (this._pendingUnsubscribeTopicId() !== null) {
      return;
    }

    this._pendingUnsubscribeTopicId.set(topic.id);
    this._errorMessage.set(null);
    this._successMessage.set(null);

    this.topicsService
      .unsubscribe(topic.id)
      .pipe(
        finalize(() => {
          this._pendingUnsubscribeTopicId.set(null);
        })
      )
      .subscribe({
        next: () => {
          this._subscriptions.set(this._subscriptions().filter((item) => item.id !== topic.id));
        },
        error: () => {
          this._errorMessage.set('Impossible de se désabonner de ce thème.');
        },
      });
  }

  private toSubscriptions(topics: TopicResponse[]): SubscriptionViewModel[] {
    return topics
      .filter((topic) => topic.subscribed)
      .map((topic) => ({
        id: topic.id,
        name: topic.name,
        description: `Retrouvez les derniers articles autour de ${topic.name}.`,
      }));
  }
}
