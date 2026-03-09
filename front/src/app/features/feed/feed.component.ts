import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, RouterLink, MatButtonModule, MatIconModule],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
})
export class FeedComponent {
  samplePosts = [
    {
      id: 1,
      title: "Titre de l'article",
      author: 'Auteur',
      date: 'Date',
      excerpt:
        "Contenu : lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard...",
    },
    {
      id: 2,
      title: "Titre de l'article",
      author: 'Auteur',
      date: 'Date',
      excerpt:
        "Contenu : lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard...",
    },
    {
      id: 3,
      title: "Titre de l'article",
      author: 'Auteur',
      date: 'Date',
      excerpt:
        "Contenu : lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard...",
    },
    {
      id: 4,
      title: "Titre de l'article",
      author: 'Auteur',
      date: 'Date',
      excerpt:
        "Contenu : lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard...",
    },
  ];
}
