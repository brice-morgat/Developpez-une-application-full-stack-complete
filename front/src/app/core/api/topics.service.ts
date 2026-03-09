import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TopicResponse } from './topics.models';

@Injectable({ providedIn: 'root' })
export class TopicsService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  getTopics(): Observable<TopicResponse[]> {
    return this.http.get<TopicResponse[]>(`${this.baseUrl}/api/topics`);
  }

  subscribe(topicId: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/api/topics/${topicId}/subscribe`, {});
  }

  unsubscribe(topicId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/topics/${topicId}/subscribe`);
  }
}
