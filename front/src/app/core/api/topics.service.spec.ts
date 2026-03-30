import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TopicsService } from './topics.service';

describe('TopicsService', () => {
  let service: TopicsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TopicsService],
    });

    service = TestBed.inject(TopicsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should get topics', () => {
    let result: unknown;
    service.getTopics().subscribe((value) => {
      result = value;
    });

    const req = httpMock.expectOne((r) => r.method === 'GET' && r.url.endsWith('/api/topics'));
    req.flush([]);
    expect(result).toEqual([]);
  });

  it('should subscribe to topic', () => {
    service.subscribe(9).subscribe();

    const req = httpMock.expectOne((r) => r.method === 'POST' && r.url.endsWith('/api/topics/9/subscribe'));
    expect(req.request.body).toEqual({});
    req.flush({});
  });

  it('should unsubscribe from topic', () => {
    let called = false;
    service.unsubscribe(9).subscribe(() => {
      called = true;
    });

    const req = httpMock.expectOne((r) => r.method === 'DELETE' && r.url.endsWith('/api/topics/9/subscribe'));
    req.flush({});
    expect(called).toBeTrue();
  });
});
