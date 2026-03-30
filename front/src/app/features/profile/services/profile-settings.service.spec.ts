import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { TopicsService } from '../../../core/api/topics.service';
import { UsersService } from '../../../core/api/users.service';
import { ProfileSettingsService } from './profile-settings.service';

describe('ProfileSettingsService', () => {
  let service: ProfileSettingsService;
  let usersApi: jasmine.SpyObj<UsersService>;
  let topicsApi: jasmine.SpyObj<TopicsService>;

  beforeEach(() => {
    usersApi = jasmine.createSpyObj<UsersService>('UsersService', ['me', 'updateMe']);
    topicsApi = jasmine.createSpyObj<TopicsService>('TopicsService', ['getTopics', 'unsubscribe']);

    TestBed.configureTestingModule({
      providers: [
        ProfileSettingsService,
        { provide: UsersService, useValue: usersApi },
        { provide: TopicsService, useValue: topicsApi },
      ],
    });

    service = TestBed.inject(ProfileSettingsService);
  });

  it('should load profile and subscribed topics', fakeAsync(() => {
    usersApi.me.and.returnValue(of({ id: 1, username: 'alice', email: 'a@a.fr', subscriptions: [2] }));
    topicsApi.getTopics.and.returnValue(
      of([
        { id: 1, name: 'Java', subscribed: false },
        { id: 2, name: 'Angular', subscribed: true },
      ])
    );

    service.loadProfileData();
    tick();

    expect(service.loading()).toBeFalse();
    expect(service.profile()?.username).toBe('alice');
    expect(service.subscriptions().length).toBe(1);
    expect(service.subscriptions()[0].id).toBe(2);
  }));

  it('should expose error when loading profile fails', fakeAsync(() => {
    usersApi.me.and.returnValue(throwError(() => new Error('boom')));
    topicsApi.getTopics.and.returnValue(of([]));

    service.loadProfileData();
    tick();

    expect(service.errorMessage()).toBe('Impossible de charger le profil utilisateur.');
    expect(service.loading()).toBeFalse();
  }));

  it('should save profile and set success message', fakeAsync(() => {
    usersApi.updateMe.and.returnValue(of({ id: 1, username: 'alice2', email: 'a2@a.fr', subscriptions: [] }));

    service.saveProfile({ username: 'alice2', email: 'a2@a.fr' });
    tick();

    expect(service.saving()).toBeFalse();
    expect(service.profile()?.username).toBe('alice2');
    expect(service.successMessage()).toBe('Profil mis à jour.');
  }));

  it('should expose api message when save profile fails', fakeAsync(() => {
    usersApi.updateMe.and.returnValue(throwError(() => ({ error: { message: 'Erreur API' } })));

    service.saveProfile({ username: 'alice2', email: 'a2@a.fr' });
    tick();

    expect(service.errorMessage()).toBe('Erreur API');
  }));

  it('should ignore save profile when already saving', () => {
    (service as any)._saving.set(true);

    service.saveProfile({ username: 'x', email: 'x@x.fr' });

    expect(usersApi.updateMe).not.toHaveBeenCalled();
  });

  it('should unsubscribe and remove topic from list', fakeAsync(() => {
    (service as any)._subscriptions.set([
      { id: 1, name: 'Java', description: 'd1' },
      { id: 2, name: 'Angular', description: 'd2' },
    ]);
    topicsApi.unsubscribe.and.returnValue(of(void 0));

    service.unsubscribe({ id: 1, name: 'Java', description: 'd1' });
    tick();

    expect(service.subscriptions().map((t) => t.id)).toEqual([2]);
    expect(service.pendingUnsubscribeTopicId()).toBeNull();
  }));

  it('should expose error when unsubscribe fails', fakeAsync(() => {
    (service as any)._subscriptions.set([{ id: 1, name: 'Java', description: 'd1' }]);
    topicsApi.unsubscribe.and.returnValue(throwError(() => new Error('boom')));

    service.unsubscribe({ id: 1, name: 'Java', description: 'd1' });
    tick();

    expect(service.errorMessage()).toBe('Impossible de se désabonner de ce thème.');
  }));

  it('should ignore unsubscribe when another request is pending', () => {
    (service as any)._pendingUnsubscribeTopicId.set(999);

    service.unsubscribe({ id: 1, name: 'Java', description: 'd1' });

    expect(topicsApi.unsubscribe).not.toHaveBeenCalled();
  });
});
