import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MeResponse, UpdateMePayload } from './users.models';

@Injectable({ providedIn: 'root' })
export class UsersService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private readonly http: HttpClient) {}

  me(): Observable<MeResponse> {
    return this.http.get<MeResponse>(`${this.baseUrl}/api/users/me`);
  }

  updateMe(payload: UpdateMePayload): Observable<MeResponse> {
    return this.http.put<MeResponse>(`${this.baseUrl}/api/users/me`, payload);
  }
}
