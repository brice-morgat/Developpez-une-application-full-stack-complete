import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UsersService } from './users.service';

describe('UsersService', () => {
  let service: UsersService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UsersService],
    });

    service = TestBed.inject(UsersService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should get current user', () => {
    let result: unknown;
    service.me().subscribe((value) => {
      result = value;
    });

    const req = httpMock.expectOne((r) => r.method === 'GET' && r.url.endsWith('/api/users/me'));
    req.flush({ id: 1, username: 'alice', email: 'a@a.fr', subscriptions: [] });
    expect(result).toEqual({ id: 1, username: 'alice', email: 'a@a.fr', subscriptions: [] });
  });

  it('should update current user', () => {
    const payload = { username: 'alice2' };

    service.updateMe(payload).subscribe();

    const req = httpMock.expectOne((r) => r.method === 'PUT' && r.url.endsWith('/api/users/me'));
    expect(req.request.body).toEqual(payload);
    req.flush({ id: 1, username: 'alice2', email: 'a@a.fr', subscriptions: [] });
  });
});
