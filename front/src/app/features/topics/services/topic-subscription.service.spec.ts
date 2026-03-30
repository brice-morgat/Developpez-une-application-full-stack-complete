import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { TopicsService } from '../../../core/api/topics.service';
import { TopicSubscriptionService, TopicViewModel } from './topic-subscription.service';

describe('TopicSubscriptionService', () => {
  let service: TopicSubscriptionService;
  let topicsApi: jasmine.SpyObj<TopicsService>;

  beforeEach(() => {
    topicsApi = jasmine.createSpyObj<TopicsService>('TopicsService', ['getTopics', 'subscribe', 'unsubscribe']);

    TestBed.configureTestingModule({
      providers: [TopicSubscriptionService, { provide: TopicsService, useValue: topicsApi }],
    });

    service = TestBed.inject(TopicSubscriptionService);
  });

  it('should load topics and enrich with description', fakeAsync(() => {
    topicsApi.getTopics.and.returnValue(of([{ id: 1, name: 'Java', subscribed: false }]));

    service.loadTopics();
    tick();

    expect(service.loading()).toBeFalse();
    expect(service.topics()[0].description).toContain('Java');
  }));

  it('should set error when load topics fails', fakeAsync(() => {
    topicsApi.getTopics.and.returnValue(throwError(() => new Error('boom')));

    service.loadTopics();
    tick();

    expect(service.errorMessage()).toBe('Impossible de charger les thèmes.');
  }));

  it('should subscribe when topic is not subscribed', fakeAsync(() => {
    const topic: TopicViewModel = { id: 1, name: 'Java', subscribed: false, description: 'd' };
    (service as any)._topics.set([topic]);
    topicsApi.subscribe.and.returnValue(of(void 0));

    service.toggleSubscription(topic);
    tick();

    expect(topicsApi.subscribe).toHaveBeenCalledWith(1);
    expect(service.topics()[0].subscribed).toBeTrue();
    expect(service.pendingTopicId()).toBeNull();
  }));

  it('should unsubscribe when topic is subscribed', fakeAsync(() => {
    const topic: TopicViewModel = { id: 1, name: 'Java', subscribed: true, description: 'd' };
    (service as any)._topics.set([topic]);
    topicsApi.unsubscribe.and.returnValue(of(void 0));

    service.toggleSubscription(topic);
    tick();

    expect(topicsApi.unsubscribe).toHaveBeenCalledWith(1);
    expect(service.topics()[0].subscribed).toBeFalse();
  }));

  it('should ignore toggle when another topic is pending', () => {
    const topic: TopicViewModel = { id: 2, name: 'Angular', subscribed: false, description: 'd' };
    (service as any)._pendingTopicId.set(1);

    service.toggleSubscription(topic);

    expect(topicsApi.subscribe).not.toHaveBeenCalled();
    expect(topicsApi.unsubscribe).not.toHaveBeenCalled();
  });

  it('should set error when toggle fails', fakeAsync(() => {
    const topic: TopicViewModel = { id: 1, name: 'Java', subscribed: false, description: 'd' };
    (service as any)._topics.set([topic]);
    topicsApi.subscribe.and.returnValue(throwError(() => new Error('boom')));

    service.toggleSubscription(topic);
    tick();

    expect(service.errorMessage()).toBe("Impossible de mettre à jour l'abonnement.");
    expect(service.pendingTopicId()).toBeNull();
  }));
});
