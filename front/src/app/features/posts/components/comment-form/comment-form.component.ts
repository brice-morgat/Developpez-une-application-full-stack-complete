import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-comment-form',
  standalone: true,
  imports: [ReactiveFormsModule, MatIconModule],
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommentFormComponent {
  readonly submitting = input(false);
  readonly submitComment = output<string>();

  protected readonly form = this.fb.nonNullable.group({
    content: ['', [Validators.required, Validators.maxLength(2000)]],
  });

  constructor(private readonly fb: FormBuilder) {}

  protected submit(): void {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    const content = this.form.controls.content.value.trim();
    if (!content) {
      this.form.controls.content.setErrors({ required: true });
      return;
    }

    this.submitComment.emit(content);
    this.form.reset();
  }
}
