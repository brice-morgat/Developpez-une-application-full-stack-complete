import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent {
  readonly form = this.fb.group({
    username: ['Username', [Validators.required]],
    email: ['email@email.fr', [Validators.required, Validators.email]],
    password: ['', [Validators.minLength(6)]],
  });

  subscriptions = [
    {
      id: 1,
      name: 'Titre du thème',
      description:
        "Description: lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard...",
    },
    {
      id: 2,
      name: 'Titre du thème',
      description:
        "Description: lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard...",
    },
  ];

  constructor(private readonly fb: FormBuilder) {}

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
    }
  }
}

