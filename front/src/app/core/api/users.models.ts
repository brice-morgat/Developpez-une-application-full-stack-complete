export interface MeResponse {
  id: number;
  email: string;
  username: string;
  subscriptions: number[];
}

export interface UpdateMePayload {
  email?: string;
  username?: string;
  password?: string;
}
