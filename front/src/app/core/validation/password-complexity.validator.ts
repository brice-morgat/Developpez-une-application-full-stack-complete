import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

const HAS_DIGIT = /\d/;
const HAS_LOWERCASE = /[a-z]/;
const HAS_UPPERCASE = /[A-Z]/;
const HAS_SPECIAL = /[^A-Za-z0-9]/;

export function passwordComplexityValidator(): ValidatorFn {
  return (control: AbstractControl<string | null>): ValidationErrors | null => {
    const value = control.value ?? '';

    if (!value) {
      return null;
    }

    const valid =
      value.length >= 8 &&
      HAS_DIGIT.test(value) &&
      HAS_LOWERCASE.test(value) &&
      HAS_UPPERCASE.test(value) &&
      HAS_SPECIAL.test(value);

    return valid ? null : { passwordComplexity: true };
  };
}
