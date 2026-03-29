import { HttpErrorResponse } from '@angular/common/http';
import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { PostsService } from '../../../core/api/posts.service';
import { TopicsService } from '../../../core/api/topics.service';
import { PostCreationService } from './post-creation.service';

describe('PostCreationService', () => {
  let service: PostCreationService;
  let topicsApi: jasmine.SpyObj<TopicsService>;
  let postsApi: jasmine.SpyObj<PostsService>;

  beforeEach(() => {
    topicsApi = jasmine.createSpyObj<TopicsService>('TopicsService', ['getTopics']);
    postsApi = jasmine.createSpyObj<PostsService>('PostsService', ['createPost']);

    TestBed.configureTestingModule({
      providers: [
        PostCreationService,
        { provide: TopicsService, useValue: topicsApi },
        { provide: PostsService, useValue: postsApi },
      ],
    });

    service = TestBed.inject(PostCreationService);
  });

  it('should load topics', fakeAsync(() => {
    topicsApi.getTopics.and.returnValue(of([{ id: 1, name: 'Java', description: 'Java desc', subscribed: false }]));

    service.loadTopics();
    tick();

    expect(service.loadingTopics()).toBeFalse();
    expect(service.topics().length).toBe(1);
  }));

  it('should expose error when loading topics fails', fakeAsync(() => {
    topicsApi.getTopics.and.returnValue(throwError(() => new Error('boom')));

    service.loadTopics();
    tick();

    expect(service.errorMessage()).toBe('Impossible de charger la liste des thèmes.');
  }));

  it('should create post and call success callback', fakeAsync(() => {
    const callback = jasmine.createSpy('callback');
    postsApi.createPost.and.returnValue(
      of({
        id: 1,
        title: 'T',
        content: 'C',
        createdAt: '2026-01-01',
        author: { id: 1, username: 'alice' },
        topic: { id: 1, name: 'Java', description: 'Java desc' },
      })
    );

    service.createPost({ topicId: 1, title: 'T', content: 'C' }, callback);
    tick();

    expect(callback).toHaveBeenCalled();
    expect(service.submitting()).toBeFalse();
  }));

  it('should ignore create post when already submitting', () => {
    (service as any)._submitting.set(true);

    service.createPost({ topicId: 1, title: 'T', content: 'C' }, () => undefined);

    expect(postsApi.createPost).not.toHaveBeenCalled();
  });

  it('should expose backend error when create post fails', fakeAsync(() => {
    postsApi.createPost.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 403, error: { message: 'Vous devez être abonné au thème pour publier un article.' } }))
    );

    service.createPost({ topicId: 1, title: 'T', content: 'C' }, () => undefined);
    tick();

    expect(service.errorMessage()).toBe('Vous devez être abonné au thème pour publier un article.');
    expect(service.submitting()).toBeFalse();
  }));

  it('should fallback to default error when backend payload is missing', fakeAsync(() => {
    postsApi.createPost.and.returnValue(throwError(() => new Error('boom')));

    service.createPost({ topicId: 1, title: 'T', content: 'C' }, () => undefined);
    tick();

    expect(service.errorMessage()).toBe("Impossible de créer l'article.");
    expect(service.submitting()).toBeFalse();
  }));
});
