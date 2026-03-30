import { HttpErrorResponse, HttpHeaders, HttpRequest } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { throwError } from 'rxjs';
import { NotificationService } from '../ui/notification.service';
import { errorToastInterceptor } from './error-toast.interceptor';

describe('errorToastInterceptor', () => {
  let notificationService: jasmine.SpyObj<NotificationService>;

  beforeEach(() => {
    notificationService = jasmine.createSpyObj<NotificationService>('NotificationService', ['showError']);
    TestBed.configureTestingModule({
      providers: [{ provide: NotificationService, useValue: notificationService }],
    });
  });

  it('should show backend error message', () => {
    const req = new HttpRequest('GET', '/api/test');

    TestBed.runInInjectionContext(() => {
      errorToastInterceptor(req, () =>
        throwError(() => new HttpErrorResponse({ status: 400, error: { message: 'Erreur backend' } }))
      ).subscribe({ error: () => undefined });
    });

    expect(notificationService.showError).toHaveBeenCalledWith('Erreur backend');
  });

  it('should show first detail when message is missing', () => {
    const req = new HttpRequest('GET', '/api/test');

    TestBed.runInInjectionContext(() => {
      errorToastInterceptor(req, () =>
        throwError(() => new HttpErrorResponse({ status: 400, error: { details: { field: 'Champ invalide' } } }))
      ).subscribe({ error: () => undefined });
    });

    expect(notificationService.showError).toHaveBeenCalledWith('Champ invalide');
  });

  it('should map network error to friendly message', () => {
    const req = new HttpRequest('GET', '/api/test');

    TestBed.runInInjectionContext(() => {
      errorToastInterceptor(req, () =>
        throwError(() => new HttpErrorResponse({ status: 0, error: null }))
      ).subscribe({ error: () => undefined });
    });

    expect(notificationService.showError).toHaveBeenCalledWith(
      'Impossible de joindre le serveur. Vérifiez votre connexion.'
    );
  });

  it('should skip toast when skip header is provided', () => {
    const req = new HttpRequest('GET', '/api/test', null, {
      headers: new HttpHeaders({ 'X-Skip-Error-Toast': '1' }),
    });

    TestBed.runInInjectionContext(() => {
      errorToastInterceptor(req, (nextReq) => {
        expect(nextReq.headers.has('X-Skip-Error-Toast')).toBeFalse();
        return throwError(() => new HttpErrorResponse({ status: 500, error: { message: 'Erreur' } }));
      }).subscribe({ error: () => undefined });
    });

    expect(notificationService.showError).not.toHaveBeenCalled();
  });
});
