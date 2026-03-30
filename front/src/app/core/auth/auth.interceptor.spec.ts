import { HttpHeaders, HttpRequest, HttpResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Store } from '@ngxs/store';
import { of } from 'rxjs';
import { AuthState } from './auth.state';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  it('should pass request unchanged when token is missing', () => {
    const store = { selectSnapshot: jasmine.createSpy().and.returnValue(null) };

    TestBed.configureTestingModule({
      providers: [{ provide: Store, useValue: store }],
    });

    const req = new HttpRequest('GET', '/api/test');
    let receivedHeaders: HttpHeaders | null = null;

    TestBed.runInInjectionContext(() => {
      authInterceptor(req, (nextReq: any) => {
        receivedHeaders = nextReq.headers;
        return of(new HttpResponse({ status: 200 }));
      }).subscribe();
    });

    expect(store.selectSnapshot).toHaveBeenCalledWith(AuthState.token);
    expect(receivedHeaders).not.toBeNull();
    expect((receivedHeaders as any)?.has('Authorization')).toBeFalse();
  });

  it('should add bearer token when token exists', () => {
    const store = { selectSnapshot: jasmine.createSpy().and.returnValue('jwt-token') };

    TestBed.configureTestingModule({
      providers: [{ provide: Store, useValue: store }],
    });

    const req = new HttpRequest('GET', '/api/test');
    let receivedHeaders: HttpHeaders | null = null;

    TestBed.runInInjectionContext(() => {
      authInterceptor(req, (nextReq: any) => {
        receivedHeaders = nextReq.headers;
        return of(new HttpResponse({ status: 200 }));
      }).subscribe();
    });

    expect(receivedHeaders).not.toBeNull();
    expect((receivedHeaders as any)?.get('Authorization')).toBe('Bearer jwt-token');
  });
});

