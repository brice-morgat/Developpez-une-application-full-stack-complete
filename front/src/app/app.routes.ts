import { Routes } from '@angular/router';
import { MainLayoutComponent } from './core/layout/main-layout.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
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
      { path: '', pathMatch: 'full', redirectTo: 'feed' },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'feed', component: FeedComponent },
      { path: 'topics', component: TopicsComponent },
      { path: 'post/:id', component: PostDetailComponent },
      { path: 'create-post', component: CreatePostComponent },
      { path: 'profile', component: ProfileComponent },
    ],
  },
  { path: '**', redirectTo: 'feed' },
];

