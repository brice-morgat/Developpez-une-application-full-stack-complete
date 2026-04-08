import { FormControl } from '@angular/forms';
import { passwordComplexityValidator } from './register.component';

describe('passwordComplexityValidator', () => {
  const validator = passwordComplexityValidator();

  it('should accept a password matching all constraints', () => {
    const control = new FormControl('Password1!');
    expect(validator(control)).toBeNull();
  });

  it('should reject a password shorter than 8 characters', () => {
    const control = new FormControl('Pwd1!');
    expect(validator(control)).toEqual({ passwordComplexity: true });
  });

  it('should reject a password without uppercase letter', () => {
    const control = new FormControl('password1!');
    expect(validator(control)).toEqual({ passwordComplexity: true });
  });

  it('should reject a password without lowercase letter', () => {
    const control = new FormControl('PASSWORD1!');
    expect(validator(control)).toEqual({ passwordComplexity: true });
  });

  it('should reject a password without digit', () => {
    const control = new FormControl('Password!');
    expect(validator(control)).toEqual({ passwordComplexity: true });
  });

  it('should reject a password without special character', () => {
    const control = new FormControl('Password1');
    expect(validator(control)).toEqual({ passwordComplexity: true });
  });
});
