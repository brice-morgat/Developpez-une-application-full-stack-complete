import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { PostsService } from '../../../core/api/posts.service';
import { PostDetailService } from './post-detail.service';

describe('PostDetailService', () => {
  let service: PostDetailService;
  let postsApi: jasmine.SpyObj<PostsService>;

  beforeEach(() => {
    postsApi = jasmine.createSpyObj<PostsService>('PostsService', ['getPost', 'createComment']);

    TestBed.configureTestingModule({
      providers: [PostDetailService, { provide: PostsService, useValue: postsApi }],
    });

    service = TestBed.inject(PostDetailService);
  });

  it('should load post detail', fakeAsync(() => {
    const post = {
      id: 1,
      title: 'T',
      content: 'C',
      createdAt: '2026-01-01',
      author: { id: 1, username: 'alice' },
      topic: { id: 1, name: 'Java' },
      comments: [],
    };
    postsApi.getPost.and.returnValue(of(post));

    service.loadPost(1);
    tick();

    expect(service.post()).toEqual(post);
    expect(service.loading()).toBeFalse();
  }));

  it('should expose error when load post fails', fakeAsync(() => {
    postsApi.getPost.and.returnValue(throwError(() => new Error('boom')));

    service.loadPost(1);
    tick();

    expect(service.errorMessage()).toBe('Impossible de charger cet article.');
  }));

  it('should add comment when submit succeeds', fakeAsync(() => {
    (service as any)._post.set({
      id: 1,
      title: 'T',
      content: 'C',
      createdAt: '2026-01-01',
      author: { id: 1, username: 'alice' },
      topic: { id: 1, name: 'Java' },
      comments: [],
    });
    postsApi.createComment.and.returnValue(
      of({ id: 10, content: 'Salut', createdAt: '2026-01-01', author: { id: 2, username: 'bob' } })
    );

    service.submitComment('Salut');
    tick();

    expect(service.post()?.comments.length).toBe(1);
    expect(service.submitting()).toBeFalse();
  }));

  it('should ignore submit comment when no post loaded', () => {
    service.submitComment('x');

    expect(postsApi.createComment).not.toHaveBeenCalled();
  });

  it('should expose error when submit comment fails', fakeAsync(() => {
    (service as any)._post.set({
      id: 1,
      title: 'T',
      content: 'C',
      createdAt: '2026-01-01',
      author: { id: 1, username: 'alice' },
      topic: { id: 1, name: 'Java' },
      comments: [],
    });
    postsApi.createComment.and.returnValue(throwError(() => new Error('boom')));

    service.submitComment('Salut');
    tick();

    expect(service.errorMessage()).toBe("Impossible d'envoyer le commentaire.");
    expect(service.submitting()).toBeFalse();
  }));

  it('should set invalid post error', () => {
    service.setInvalidPostError();

    expect(service.loading()).toBeFalse();
    expect(service.errorMessage()).toBe('Article introuvable.');
  });
});
