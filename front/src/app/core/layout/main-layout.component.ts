import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Store } from '@ngxs/store';
import { filter } from 'rxjs/operators';
import { Logout } from '../auth/auth.actions';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, MatButtonModule, MatIconModule],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss'],
})
export class MainLayoutComponent {
  showMainHeader = false;
  showAuthHeader = false;
  mobileMenuOpen = false;

  constructor(private readonly router: Router, private readonly store: Store) {
    this.updateRoute(this.router.url);
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe(() => {
      this.updateRoute(this.router.url);
      this.mobileMenuOpen = false;
    });
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.mobileMenuOpen = false;
  }

  logout(): void {
    this.store.dispatch(new Logout());
  }

  private updateRoute(url: string): void {
    this.showAuthHeader = url.startsWith('/login') || url.startsWith('/register');
    this.showMainHeader = !this.showAuthHeader && url !== '/';
  }
}
