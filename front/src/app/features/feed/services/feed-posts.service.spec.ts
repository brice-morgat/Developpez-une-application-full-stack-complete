import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { PostsService } from '../../../core/api/posts.service';
import { FeedPostsService } from './feed-posts.service';

describe('FeedPostsService', () => {
  let service: FeedPostsService;
  let postsApi: jasmine.SpyObj<PostsService>;

  beforeEach(() => {
    postsApi = jasmine.createSpyObj<PostsService>('PostsService', ['getFeed']);

    TestBed.configureTestingModule({
      providers: [FeedPostsService, { provide: PostsService, useValue: postsApi }],
    });

    service = TestBed.inject(FeedPostsService);
  });

  it('should load posts successfully', fakeAsync(() => {
    const posts = [
      {
        id: 1,
        title: 'Titre',
        content: 'Contenu',
        createdAt: '2026-01-01T00:00:00Z',
        author: { id: 1, username: 'alice' },
        topic: { id: 1, name: 'Angular' },
      },
    ];
    postsApi.getFeed.and.returnValue(of(posts));

    service.loadPosts();
    tick();

    expect(postsApi.getFeed).toHaveBeenCalledWith('desc');
    expect(service.posts()).toEqual(posts);
    expect(service.loading()).toBeFalse();
    expect(service.errorMessage()).toBeNull();
  }));

  it('should expose error when load fails', fakeAsync(() => {
    postsApi.getFeed.and.returnValue(throwError(() => new Error('boom')));

    service.loadPosts();
    tick();

    expect(service.loading()).toBeFalse();
    expect(service.errorMessage()).toBe("Impossible de charger les articles.");
  }));

  it('should toggle sort and reload posts', () => {
    postsApi.getFeed.and.returnValue(of([]));

    service.toggleSort();

    expect(service.sort()).toBe('asc');
    expect(postsApi.getFeed).toHaveBeenCalledWith('asc');
  });
});
