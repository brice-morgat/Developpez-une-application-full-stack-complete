import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { SubscriptionCardComponent } from './subscription-card.component';

describe('SubscriptionCardComponent', () => {
  let fixture: ComponentFixture<SubscriptionCardComponent>;
  let component: SubscriptionCardComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SubscriptionCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('topic', { id: 1, name: 'Java', description: 'Desc' });
    fixture.detectChanges();
  });

  it('should emit topic on unsubscribe click', () => {
    const spy = jasmine.createSpy('unsubscribe');
    component.unsubscribe.subscribe(spy);

    fixture.debugElement.query(By.css('button')).nativeElement.click();

    expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ id: 1 }));
  });

  it('should disable button when pending', () => {
    fixture.componentRef.setInput('pending', true);
    fixture.detectChanges();

    const button: HTMLButtonElement = fixture.debugElement.query(By.css('button')).nativeElement;
    expect(button.disabled).toBeTrue();
  });
});
