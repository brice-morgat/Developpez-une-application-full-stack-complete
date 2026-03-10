import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { MainLayoutComponent } from './core/layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', loadComponent: () => import('./features/auth/welcome/welcome.component').then((m) => m.WelcomeComponent) },
      { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent) },
      { path: 'register', loadComponent: () => import('./features/auth/register/register.component').then((m) => m.RegisterComponent) },
      {
        path: 'feed',
        canActivate: [authGuard],
        loadComponent: () => import('./features/feed/feed.component').then((m) => m.FeedComponent),
      },
      {
        path: 'topics',
        canActivate: [authGuard],
        loadComponent: () => import('./features/topics/topics.component').then((m) => m.TopicsComponent),
      },
      {
        path: 'post/:id',
        canActivate: [authGuard],
        loadComponent: () => import('./features/posts/post-detail/post-detail.component').then((m) => m.PostDetailComponent),
      },
      {
        path: 'create-post',
        canActivate: [authGuard],
        loadComponent: () => import('./features/posts/create-post/create-post.component').then((m) => m.CreatePostComponent),
      },
      {
        path: 'profile',
        canActivate: [authGuard],
        loadComponent: () => import('./features/profile/profile.component').then((m) => m.ProfileComponent),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];

