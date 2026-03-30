import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { PostCardComponent } from './post-card.component';

describe('PostCardComponent', () => {
  let fixture: ComponentFixture<PostCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PostCardComponent, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(PostCardComponent);
    fixture.componentRef.setInput('post', {
      id: 10,
      title: 'Titre de test',
      content: 'Contenu de test',
      createdAt: '2026-01-01T00:00:00Z',
      author: { id: 1, username: 'alice' },
      topic: { id: 2, name: 'Angular' },
    });
    fixture.detectChanges();
  });

  it('should render post title and author', () => {
    const text = fixture.nativeElement.textContent;
    expect(text).toContain('Titre de test');
    expect(text).toContain('alice');
  });
});
