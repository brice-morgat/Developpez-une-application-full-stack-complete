import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { Login, Logout, Register } from './auth.actions';
import { AuthState, AuthStateModel } from './auth.state';

describe('AuthState', () => {
  let state: AuthState;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['login', 'register']);

    TestBed.configureTestingModule({
      providers: [AuthState, { provide: AuthService, useValue: authService }],
    });

    state = TestBed.inject(AuthState);
  });

  function createCtx(initial?: Partial<AuthStateModel>) {
    let snapshot: AuthStateModel = {
      token: null,
      user: null,
      loading: false,
      error: null,
      ...initial,
    };

    return {
      getState: () => snapshot,
      patchState: (partial: Partial<AuthStateModel>) => {
        snapshot = { ...snapshot, ...partial };
      },
      setState: (value: AuthStateModel) => {
        snapshot = value;
      },
      snapshot: () => snapshot,
    };
  }

  it('should register successfully', () => {
    const ctx = createCtx();
    const payload = { email: 'a@a.fr', username: 'alice', password: 'secret123' };
    authService.register.and.returnValue(of({ token: 'jwt', user: { id: 1, email: 'a@a.fr', username: 'alice' } }));

    state.register(ctx as any, new Register(payload)).subscribe();

    expect(ctx.snapshot().token).toBe('jwt');
    expect(ctx.snapshot().loading).toBeFalse();
    expect(ctx.snapshot().error).toBeNull();
  });

  it('should set register error message from backend', () => {
    const ctx = createCtx();
    const payload = { email: 'a@a.fr', username: 'alice', password: 'secret123' };
    authService.register.and.returnValue(throwError(() => ({ error: { message: 'Erreur inscription' } })));

    state.register(ctx as any, new Register(payload)).subscribe({ error: () => undefined });

    expect(ctx.snapshot().loading).toBeFalse();
    expect(ctx.snapshot().error).toBe('Erreur inscription');
  });

  it('should login successfully', () => {
    const ctx = createCtx();
    const payload = { identifier: 'alice', password: 'secret123' };
    authService.login.and.returnValue(of({ token: 'jwt', user: { id: 1, email: 'a@a.fr', username: 'alice' } }));

    state.login(ctx as any, new Login(payload)).subscribe();

    expect(ctx.snapshot().token).toBe('jwt');
    expect(ctx.snapshot().loading).toBeFalse();
    expect(ctx.snapshot().error).toBeNull();
  });

  it('should use fallback login error message', () => {
    const ctx = createCtx();
    const payload = { identifier: 'alice', password: 'secret123' };
    authService.login.and.returnValue(throwError(() => ({ error: {} })));

    state.login(ctx as any, new Login(payload)).subscribe({ error: () => undefined });

    expect(ctx.snapshot().error).toBe('La connexion a échoué.');
  });

  it('should reset state on logout', () => {
    const ctx = createCtx({ token: 'jwt', user: { id: 1, email: 'a@a.fr', username: 'alice' } });

    state.logout(ctx as any);

    expect(ctx.snapshot()).toEqual({ token: null, user: null, loading: false, error: null });
  });

  it('selectors should return expected values', () => {
    const model: AuthStateModel = {
      token: 'jwt',
      user: { id: 1, email: 'a@a.fr', username: 'alice' },
      loading: true,
      error: 'err',
    };

    expect(AuthState.token(model)).toBe('jwt');
    expect(AuthState.user(model)?.username).toBe('alice');
    expect(AuthState.isAuthenticated(model)).toBeTrue();
    expect(AuthState.loading(model)).toBeTrue();
    expect(AuthState.error(model)).toBe('err');
  });
});

