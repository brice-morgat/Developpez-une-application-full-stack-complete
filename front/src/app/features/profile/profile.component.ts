import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { finalize, forkJoin } from 'rxjs';
import { TopicResponse } from '../../core/api/topics.models';
import { TopicsService } from '../../core/api/topics.service';
import { UsersService } from '../../core/api/users.service';

interface SubscriptionViewModel {
  id: number;
  name: string;
  description: string;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly usersService = inject(UsersService);
  private readonly topicsService = inject(TopicsService);

  readonly form = this.fb.group({
    username: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.minLength(6)]],
  });

  subscriptions: SubscriptionViewModel[] = [];
  loading = true;
  saving = false;
  pendingUnsubscribeTopicId: number | null = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  ngOnInit(): void {
    this.loadProfileData();
  }

  save(): void {
    if (this.form.invalid || this.saving) {
      this.form.markAllAsTouched();
      return;
    }

    const username = this.form.controls.username.value?.trim();
    const email = this.form.controls.email.value?.trim();
    const password = this.form.controls.password.value?.trim();

    if (!username || !email) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.usersService
      .updateMe({
        username,
        email,
        password: password ? password : undefined,
      })
      .pipe(
        finalize(() => {
          this.saving = false;
        })
      )
      .subscribe({
        next: (user) => {
          this.form.patchValue({
            username: user.username,
            email: user.email,
            password: '',
          });
          this.successMessage = 'Profil mis à jour.';
        },
        error: (error) => {
          this.errorMessage = error?.error?.message ?? 'Impossible de mettre à jour le profil.';
        },
      });
  }

  unsubscribe(topic: SubscriptionViewModel): void {
    if (this.pendingUnsubscribeTopicId !== null) {
      return;
    }

    this.pendingUnsubscribeTopicId = topic.id;
    this.errorMessage = null;
    this.successMessage = null;

    this.topicsService
      .unsubscribe(topic.id)
      .pipe(
        finalize(() => {
          this.pendingUnsubscribeTopicId = null;
        })
      )
      .subscribe({
        next: () => {
          this.subscriptions = this.subscriptions.filter((item) => item.id !== topic.id);
        },
        error: () => {
          this.errorMessage = 'Impossible de se désabonner de ce thème.';
        },
      });
  }

  private loadProfileData(): void {
    this.loading = true;
    this.errorMessage = null;

    forkJoin({
      user: this.usersService.me(),
      topics: this.topicsService.getTopics(),
    })
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: ({ user, topics }) => {
          this.form.patchValue({
            username: user.username,
            email: user.email,
            password: '',
          });
          this.subscriptions = this.toSubscriptions(topics);
        },
        error: () => {
          this.errorMessage = 'Impossible de charger le profil utilisateur.';
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
