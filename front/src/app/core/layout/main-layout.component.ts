import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, MatToolbarModule, MatButtonModule],
  template: `
    <mat-toolbar class="topbar" color="primary">
      <span class="brand">MDD</span>
      <nav class="nav-links">
        <a mat-button routerLink="/feed" routerLinkActive="active">Feed</a>
        <a mat-button routerLink="/topics" routerLinkActive="active">Topics</a>
        <a mat-button routerLink="/create-post" routerLinkActive="active">Create</a>
        <a mat-button routerLink="/profile" routerLinkActive="active">Profile</a>
      </nav>
    </mat-toolbar>
    <main class="page-shell">
      <router-outlet></router-outlet>
    </main>
  `,
})
export class MainLayoutComponent {}
