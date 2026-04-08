import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter } from '@angular/router';
import { importProvidersFrom } from '@angular/core';
import { provideStore, withNgxsDevelopmentOptions } from '@ngxs/store';
import { withNgxsStoragePlugin } from '@ngxs/storage-plugin';
import { authInterceptor } from './core/auth/auth.interceptor';
import { errorToastInterceptor } from './core/http/error-toast.interceptor';
import { AuthState } from './core/auth/auth.state';
import { routes } from './app.routes';
import { AppState } from './core/state/app.state';
import { MatSnackBarModule } from '@angular/material/snack-bar';

export const appConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor, errorToastInterceptor])),
    provideAnimationsAsync(),
    importProvidersFrom(MatSnackBarModule),
    provideStore(
      [AppState, AuthState],
      withNgxsStoragePlugin({ keys: ['auth'] }),
      withNgxsDevelopmentOptions({ warnOnUnhandledActions: true })
    ),
  ],
};
