import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { CommentListComponent } from './comment-list.component';

describe('CommentListComponent', () => {
  let fixture: ComponentFixture<CommentListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommentListComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CommentListComponent);
    fixture.componentRef.setInput('comments', [
      { id: 1, content: 'Salut', createdAt: '2026-01-01', author: { id: 1, username: 'alice' } },
      { id: 2, content: 'Hello', createdAt: '2026-01-01', author: { id: 2, username: 'bob' } },
    ]);
    fixture.detectChanges();
  });

  it('should render comments', () => {
    const items = fixture.debugElement.queryAll(By.css('.comment-row'));
    expect(items.length).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('alice');
    expect(fixture.nativeElement.textContent).toContain('Hello');
  });
});
