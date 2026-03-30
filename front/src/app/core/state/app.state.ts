import { Action, createReducer } from '@ngrx/store';

export interface AppState {
  isInitialized: boolean;
}

const initialState: AppState = {
  isInitialized: true,
};

const appReducerInternal = createReducer(initialState);

export function appReducer(state: AppState | undefined, action: Action): AppState {
  return appReducerInternal(state, action);
}

