export interface AuthUser {
  id: number;
  email: string;
  username: string;
}

export interface AuthResponse {
  token: string;
  user: AuthUser;
}

export interface RegisterPayload {
  email: string;
  username: string;
  password: string;
}

export interface LoginPayload {
  identifier: string;
  password: string;
}

