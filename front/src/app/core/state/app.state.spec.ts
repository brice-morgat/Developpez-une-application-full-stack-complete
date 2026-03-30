import { AppState } from './app.state';

describe('AppState', () => {
  it('should return isInitialized selector value', () => {
    expect(AppState.isInitialized({ isInitialized: true })).toBeTrue();
    expect(AppState.isInitialized({ isInitialized: false })).toBeFalse();
  });
});
