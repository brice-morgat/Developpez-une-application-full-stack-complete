import { Injectable } from '@angular/core';
import { Selector, State } from '@ngxs/store';

export interface AppStateModel {
  isInitialized: boolean;
}

@State<AppStateModel>({
  name: 'app',
  defaults: {
    isInitialized: true,
  },
})
@Injectable()
export class AppState {
  @Selector()
  static isInitialized(state: AppStateModel): boolean {
    return state.isInitialized;
  }
}

