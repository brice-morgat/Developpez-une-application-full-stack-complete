import { Subject, of } from 'rxjs';
import { NavigationEnd, Router } from '@angular/router';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Store } from '@ngxs/store';
import { MainLayoutComponent } from './main-layout.component';

describe('MainLayoutComponent', () => {
  let fixture: ComponentFixture<MainLayoutComponent>;
  let routerEvents$: Subject<NavigationEnd>;
  let routerMock: { url: string; events: Subject<NavigationEnd>; navigateByUrl: jasmine.Spy };

  beforeEach(async () => {
    routerEvents$ = new Subject<NavigationEnd>();
    routerMock = {
      url: '/',
      events: routerEvents$,
      navigateByUrl: jasmine.createSpy('navigateByUrl').and.returnValue(Promise.resolve(true)),
    };

    await TestBed.configureTestingModule({
      imports: [MainLayoutComponent],
      providers: [
        {
          provide: Router,
          useValue: routerMock,
        },
        {
          provide: Store,
          useValue: {
            dispatch: jasmine.createSpy('dispatch').and.returnValue(of(void 0)),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MainLayoutComponent);
    fixture.detectChanges();
  });

  it('hides both headers on the welcome page', () => {
    const component = fixture.componentInstance as MainLayoutComponent & {
      isWelcomeRoute: boolean;
      showAuthHeader: boolean;
      showMainHeader: boolean;
    };

    expect(component.isWelcomeRoute).toBeTrue();
    expect(component.showAuthHeader).toBeFalse();
    expect(component.showMainHeader).toBeFalse();
  });

  it('shows the auth header on login', () => {
    routerMock.url = '/login';
    routerEvents$.next(new NavigationEnd(1, '/login', '/login'));
    fixture.detectChanges();

    const component = fixture.componentInstance as MainLayoutComponent & {
      showAuthHeader: boolean;
      showMainHeader: boolean;
    };

    expect(component.showAuthHeader).toBeTrue();
    expect(component.showMainHeader).toBeFalse();
  });

  it('shows the main header on authenticated pages', () => {
    routerMock.url = '/feed';
    routerEvents$.next(new NavigationEnd(1, '/feed', '/feed'));
    fixture.detectChanges();

    const component = fixture.componentInstance as MainLayoutComponent & {
      isWelcomeRoute: boolean;
      showAuthHeader: boolean;
      showMainHeader: boolean;
    };

    expect(component.isWelcomeRoute).toBeFalse();
    expect(component.showAuthHeader).toBeFalse();
    expect(component.showMainHeader).toBeTrue();
  });
});
