import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { MainLayoutComponent } from './core/layout/main-layout.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { WelcomeComponent } from './features/auth/welcome/welcome.component';
import { FeedComponent } from './features/feed/feed.component';
import { CreatePostComponent } from './features/posts/create-post/create-post.component';
import { PostDetailComponent } from './features/posts/post-detail/post-detail.component';
import { ProfileComponent } from './features/profile/profile.component';
import { TopicsComponent } from './features/topics/topics.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', component: WelcomeComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'feed', component: FeedComponent, canActivate: [authGuard] },
      { path: 'topics', component: TopicsComponent, canActivate: [authGuard] },
      { path: 'post/:id', component: PostDetailComponent, canActivate: [authGuard] },
      { path: 'create-post', component: CreatePostComponent, canActivate: [authGuard] },
      { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
    ],
  },
  { path: '**', redirectTo: '' },
];

