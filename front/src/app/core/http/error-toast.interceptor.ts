import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ApiError } from '../api/api-error.model';
import { NotificationService } from '../ui/notification.service';

const SKIP_TOAST_HEADER = 'X-Skip-Error-Toast';

export const errorToastInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);
  const skipToast = req.headers.has(SKIP_TOAST_HEADER);
  const request = req.clone({
    headers: req.headers.delete(SKIP_TOAST_HEADER),
  });

  return next(request).pipe(
    catchError((error: unknown) => {
      if (!skipToast && error instanceof HttpErrorResponse) {
        notificationService.showError(extractErrorMessage(error));
      }
      return throwError(() => error);
    })
  );
};

function extractErrorMessage(error: HttpErrorResponse): string {
  if (error.status === 0) {
    return 'Impossible de joindre le serveur. Vérifiez votre connexion.';
  }

  const payload = error.error as ApiError | string | null;
  if (payload && typeof payload === 'object') {
    const explicitMessage = payload.message?.trim();
    if (explicitMessage) {
      return explicitMessage;
    }

    const detailEntry = payload.details ? Object.values(payload.details)[0] : null;
    if (detailEntry) {
      return detailEntry;
    }
  }

  if (typeof payload === 'string' && payload.trim()) {
    return payload;
  }

  return 'Une erreur est survenue.';
}
