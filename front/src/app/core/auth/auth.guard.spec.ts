import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Store } from '@ngxs/store';
import { authGuard } from './auth.guard';
import { AuthState } from './auth.state';

describe('authGuard', () => {
  it('should return true when authenticated', () => {
    const store = { selectSnapshot: jasmine.createSpy().and.returnValue(true) };
    const router = { createUrlTree: jasmine.createSpy() };

    TestBed.configureTestingModule({
      providers: [
        { provide: Store, useValue: store },
        { provide: Router, useValue: router },
      ],
    });

    const result = TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));

    expect(store.selectSnapshot).toHaveBeenCalledWith(AuthState.isAuthenticated);
    expect(result).toBeTrue();
    expect(router.createUrlTree).not.toHaveBeenCalled();
  });

  it('should redirect to login when unauthenticated', () => {
    const tree = {} as any;
    const store = { selectSnapshot: jasmine.createSpy().and.returnValue(false) };
    const router = { createUrlTree: jasmine.createSpy().and.returnValue(tree) };

    TestBed.configureTestingModule({
      providers: [
        { provide: Store, useValue: store },
        { provide: Router, useValue: router },
      ],
    });

    const result = TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));

    expect(router.createUrlTree).toHaveBeenCalledWith(['/login']);
    expect(result).toBe(tree);
  });
});
