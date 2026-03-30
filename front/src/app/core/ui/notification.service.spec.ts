import { TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  it('should open snack bar with error styles', () => {
    const snackBar = jasmine.createSpyObj<MatSnackBar>('MatSnackBar', ['open']);

    TestBed.configureTestingModule({
      providers: [NotificationService, { provide: MatSnackBar, useValue: snackBar }],
    });

    const service = TestBed.inject(NotificationService);
    service.showError('Erreur test');

    expect(snackBar.open).toHaveBeenCalled();
    const args = snackBar.open.calls.mostRecent().args;
    expect(args[0]).toBe('Erreur test');
    expect(args[1]).toBe('Fermer');
    expect(args[2]?.panelClass).toEqual(['mdd-toast-error']);
  });
});
