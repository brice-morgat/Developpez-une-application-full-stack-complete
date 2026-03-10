import { Injectable } from '@angular/core';
import { Action, Selector, State, StateContext } from '@ngxs/store';
import { catchError, tap, throwError } from 'rxjs';
import { Login, Logout, Register } from './auth.actions';
import { AuthService } from './auth.service';
import { AuthUser } from './auth.models';

export interface AuthStateModel {
  token: string | null;
  user: AuthUser | null;
  loading: boolean;
  error: string | null;
}

@State<AuthStateModel>({
  name: 'auth',
  defaults: {
    token: null,
    user: null,
    loading: false,
    error: null,
  },
})
@Injectable()
export class AuthState {
  constructor(private readonly authService: AuthService) {}

  @Selector()
  static token(state: AuthStateModel): string | null {
    return state.token;
  }

  @Selector()
  static user(state: AuthStateModel): AuthUser | null {
    return state.user;
  }

  @Selector()
  static isAuthenticated(state: AuthStateModel): boolean {
    return !!state.token;
  }

  @Selector()
  static loading(state: AuthStateModel): boolean {
    return state.loading;
  }

  @Selector()
  static error(state: AuthStateModel): string | null {
    return state.error;
  }

  @Action(Register)
  register(ctx: StateContext<AuthStateModel>, action: Register) {
    ctx.patchState({ loading: true, error: null });
    return this.authService.register(action.payload).pipe(
      tap((response) => {
        ctx.patchState({ token: response.token, user: response.user, loading: false, error: null });
      }),
      catchError((error) => {
        const message = error?.error?.message ?? "L'inscription a échoué.";
        ctx.patchState({ loading: false, error: message });
        return throwError(() => error);
      })
    );
  }

  @Action(Login)
  login(ctx: StateContext<AuthStateModel>, action: Login) {
    ctx.patchState({ loading: true, error: null });
    return this.authService.login(action.payload).pipe(
      tap((response) => {
        ctx.patchState({ token: response.token, user: response.user, loading: false, error: null });
      }),
      catchError((error) => {
        const message = error?.error?.message ?? 'La connexion a échoué.';
        ctx.patchState({ loading: false, error: message });
        return throwError(() => error);
      })
    );
  }

  @Action(Logout)
  logout(ctx: StateContext<AuthStateModel>) {
    ctx.setState({ token: null, user: null, loading: false, error: null });
  }
}
