import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, MatToolbarModule, MatButtonModule],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss'],
})
export class MainLayoutComponent {
  showMainHeader = false;
  showAuthHeader = false;

  constructor(private readonly router: Router) {
    this.updateRoute(this.router.url);
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe(() => {
      this.updateRoute(this.router.url);
    });
  }

  private updateRoute(url: string): void {
    this.showAuthHeader = url.startsWith('/login') || url.startsWith('/register');
    this.showMainHeader = !this.showAuthHeader && url !== '/';
  }
}
