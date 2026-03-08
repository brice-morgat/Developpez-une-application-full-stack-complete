import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { provideStore } from '@ngxs/store';
import { routes } from './app.routes';
import { AppState } from './core/state/app.state';

export const appConfig = {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    provideStore([AppState]),
  ],
};

