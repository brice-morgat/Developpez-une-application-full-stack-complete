import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call register endpoint', () => {
    const payload = { email: 'a@a.fr', username: 'alice', password: 'secret123' };

    service.register(payload).subscribe();

    const req = httpMock.expectOne((r) => r.method === 'POST' && r.url.endsWith('/api/auth/register'));
    expect(req.request.body).toEqual(payload);
    req.flush({ token: 'jwt', user: { id: 1, email: payload.email, username: payload.username } });
  });

  it('should call login endpoint', () => {
    const payload = { identifier: 'alice', password: 'secret123' };

    service.login(payload).subscribe();

    const req = httpMock.expectOne((r) => r.method === 'POST' && r.url.endsWith('/api/auth/login'));
    expect(req.request.body).toEqual(payload);
    req.flush({ token: 'jwt', user: { id: 1, email: 'a@a.fr', username: 'alice' } });
  });
});
