type AuthUser = {
  id: number;
  email: string;
  username: string;
};

type VisitAppOptions = {
  authenticated?: boolean;
  token?: string;
  user?: Partial<AuthUser>;
};

const defaultUser: AuthUser = {
  id: 1,
  email: 'alice@mdd.dev',
  username: 'alice',
};

const buildAuthState = (options: VisitAppOptions = {}) => ({
  token: options.token ?? 'jwt-token',
  user: {
    ...defaultUser,
    ...options.user,
  },
  loading: false,
  error: null,
});

declare global {
  namespace Cypress {
    interface Chainable {
      getBySel(selector: string): Chainable<JQuery<HTMLElement>>;
      visitApp(path?: string, options?: VisitAppOptions): Chainable<Window>;
      selectMatOption(selector: string, optionText: string): Chainable<JQuery<HTMLElement>>;
    }
  }
}

Cypress.Commands.add('getBySel', (selector: string) => cy.get(`[data-cy="${selector}"]`));

Cypress.Commands.add('visitApp', (path = '/', options: VisitAppOptions = {}) =>
  cy.visit(path, {
    onBeforeLoad(window) {
      if (options.authenticated) {
        window.localStorage.setItem('auth', JSON.stringify(buildAuthState(options)));
        return;
      }

      window.localStorage.removeItem('auth');
    },
  })
);

Cypress.Commands.add('selectMatOption', (selector: string, optionText: string) => {
  cy.getBySel(selector).click();
  return cy.get('mat-option').contains(optionText).click();
});

export {};
