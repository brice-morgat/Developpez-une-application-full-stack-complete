import { LoginPayload, RegisterPayload } from './auth.models';

export class Register {
  static readonly type = '[Auth] Register';
  constructor(public payload: RegisterPayload) {}
}

export class Login {
  static readonly type = '[Auth] Login';
  constructor(public payload: LoginPayload) {}
}

export class Logout {
  static readonly type = '[Auth] Logout';
}

export class RestoreSession {
  static readonly type = '[Auth] Restore Session';
}

