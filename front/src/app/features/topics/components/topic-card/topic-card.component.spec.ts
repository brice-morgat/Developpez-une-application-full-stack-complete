import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { TopicCardComponent } from './topic-card.component';

describe('TopicCardComponent', () => {
  let fixture: ComponentFixture<TopicCardComponent>;
  let component: TopicCardComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopicCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TopicCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('topic', {
      id: 1,
      name: 'Angular',
      subscribed: false,
      description: 'Desc',
    });
    fixture.detectChanges();
  });

  it('should emit topic on toggle', () => {
    const spy = jasmine.createSpy('toggle');
    component.toggle.subscribe(spy);

    fixture.debugElement.query(By.css('button')).nativeElement.click();

    expect(spy).toHaveBeenCalledWith(jasmine.objectContaining({ id: 1, name: 'Angular' }));
  });

  it('should disable button when pending', () => {
    fixture.componentRef.setInput('pending', true);
    fixture.detectChanges();

    const button: HTMLButtonElement = fixture.debugElement.query(By.css('button')).nativeElement;
    expect(button.disabled).toBeTrue();
  });
});
