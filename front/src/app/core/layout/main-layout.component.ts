import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Store } from '@ngxs/store';
import { filter } from 'rxjs/operators';
import { Logout } from '../auth/auth.actions';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, MatButtonModule, MatIconModule],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MainLayoutComponent {
  private readonly router = inject(Router);
  private readonly store = inject(Store);
  private readonly destroyRef = inject(DestroyRef);

  protected showMainHeader = false;
  protected showAuthHeader = false;
  protected mobileMenuOpen = false;

  constructor() {
    this.updateRoute(this.router.url);
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => {
        this.updateRoute(this.router.url);
        this.mobileMenuOpen = false;
      });
  }

  protected toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  protected closeMobileMenu(): void {
    this.mobileMenuOpen = false;
  }

  protected logout(): void {
    this.store.dispatch(new Logout()).subscribe(() => {
      void this.router.navigateByUrl('/login');
    });
  }

  private updateRoute(url: string): void {
    this.showAuthHeader = url.startsWith('/login') || url.startsWith('/register');
    this.showMainHeader = !this.showAuthHeader && url !== '/';
  }
}
