import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { provideStore, withNgxsDevelopmentOptions } from '@ngxs/store';
import { withNgxsStoragePlugin } from '@ngxs/storage-plugin';
import { authInterceptor } from './core/auth/auth.interceptor';
import { AuthState } from './core/auth/auth.state';
import { routes } from './app.routes';
import { AppState } from './core/state/app.state';

export const appConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimations(),
    provideStore(
      [AppState, AuthState],
      withNgxsStoragePlugin({ keys: ['auth'] }),
      withNgxsDevelopmentOptions({ warnOnUnhandledActions: true })
    ),
  ],
};
