import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { CommentFormComponent } from './comment-form.component';

describe('CommentFormComponent', () => {
  let fixture: ComponentFixture<CommentFormComponent>;
  let component: CommentFormComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommentFormComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CommentFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should emit trimmed comment when form is valid', () => {
    const spy = jasmine.createSpy('submitComment');
    component.submitComment.subscribe(spy);
    (component as any).form.controls.content.setValue('  Bonjour  ');

    const form = fixture.debugElement.query(By.css('form'));
    form.triggerEventHandler('ngSubmit', {});

    expect(spy).toHaveBeenCalledWith('Bonjour');
    expect((component as any).form.controls.content.value).toBe('');
  });

  it('should not emit when form is invalid', () => {
    const spy = jasmine.createSpy('submitComment');
    component.submitComment.subscribe(spy);
    (component as any).form.controls.content.setValue('');

    const form = fixture.debugElement.query(By.css('form'));
    form.triggerEventHandler('ngSubmit', {});

    expect(spy).not.toHaveBeenCalled();
  });

  it('should not emit when submitting input is true', () => {
    const spy = jasmine.createSpy('submitComment');
    component.submitComment.subscribe(spy);
    fixture.componentRef.setInput('submitting', true);
    (component as any).form.controls.content.setValue('Bonjour');

    const form = fixture.debugElement.query(By.css('form'));
    form.triggerEventHandler('ngSubmit', {});

    expect(spy).not.toHaveBeenCalled();
  });
});
