import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PostsService } from './posts.service';

describe('PostsService', () => {
  let service: PostsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PostsService],
    });

    service = TestBed.inject(PostsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should request feed with default desc sort', () => {
    service.getFeed().subscribe();

    const req = httpMock.expectOne((r) => r.method === 'GET' && r.url.endsWith('/api/feed'));
    expect(req.request.params.get('sort')).toBe('desc');
    req.flush([]);
  });

  it('should request feed with asc sort', () => {
    service.getFeed('asc').subscribe();

    const req = httpMock.expectOne((r) => r.method === 'GET' && r.url.endsWith('/api/feed'));
    expect(req.request.params.get('sort')).toBe('asc');
    req.flush([]);
  });

  it('should request post detail by id', () => {
    let result: unknown;
    service.getPost(12).subscribe((value) => {
      result = value;
    });

    const req = httpMock.expectOne((r) => r.method === 'GET' && r.url.endsWith('/api/posts/12'));
    req.flush({ id: 12, title: 'Article' });
    expect(result).toEqual({ id: 12, title: 'Article' });
  });

  it('should create post', () => {
    const payload = { topicId: 1, title: 'Titre', content: 'Contenu' };

    service.createPost(payload).subscribe();

    const req = httpMock.expectOne((r) => r.method === 'POST' && r.url.endsWith('/api/posts'));
    expect(req.request.body).toEqual(payload);
    req.flush({ id: 1 });
  });

  it('should create comment', () => {
    const payload = { content: 'Salut' };

    service.createComment(42, payload).subscribe();

    const req = httpMock.expectOne((r) => r.method === 'POST' && r.url.endsWith('/api/posts/42/comments'));
    expect(req.request.body).toEqual(payload);
    req.flush({ id: 10 });
  });
});
