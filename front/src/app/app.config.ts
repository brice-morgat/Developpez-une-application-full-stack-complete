import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { provideStore } from '@ngxs/store';
import { authInterceptor } from './core/auth/auth.interceptor';
import { AuthState } from './core/auth/auth.state';
import { routes } from './app.routes';
import { AppState } from './core/state/app.state';

export const appConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimations(),
    provideStore([AppState, AuthState]),
  ],
};

