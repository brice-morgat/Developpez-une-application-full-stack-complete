import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateCommentPayload, CreatePostPayload, FeedPost, PostComment, PostDetail } from './posts.models';

@Injectable({ providedIn: 'root' })
export class PostsService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  getFeed(sort: 'asc' | 'desc' = 'desc'): Observable<FeedPost[]> {
    return this.http.get<FeedPost[]>(`${this.baseUrl}/api/feed`, {
      params: { sort },
    });
  }

  getPost(postId: number): Observable<PostDetail> {
    return this.http.get<PostDetail>(`${this.baseUrl}/api/posts/${postId}`);
  }

  createPost(payload: CreatePostPayload): Observable<FeedPost> {
    return this.http.post<FeedPost>(`${this.baseUrl}/api/posts`, payload);
  }

  createComment(postId: number, payload: CreateCommentPayload): Observable<PostComment> {
    return this.http.post<PostComment>(`${this.baseUrl}/api/posts/${postId}/comments`, payload);
  }
}
