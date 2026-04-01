import { authenticatedUser } from '../support/mock-data';

describe('Authentication flow', () => {
  it('shows the backend message when login fails', () => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 401,
      body: {
        message: 'Identifiants invalides.',
      },
    }).as('login');

    cy.visitApp('/login');

    cy.getBySel('login-identifier').closest('mat-form-field').click();
    cy.getBySel('login-identifier').type('alice');
    cy.getBySel('login-password').closest('mat-form-field').click();
    cy.getBySel('login-password').type('wrong-password');
    cy.getBySel('login-submit').click();

    cy.wait('@login');
    cy.getBySel('login-error').should('contain', 'Identifiants invalides.');
  });

  it('logs in and reuses the JWT bearer token on the feed request', () => {
    cy.intercept('POST', '**/api/auth/login', (request) => {
      expect(request.body).to.deep.equal({
        identifier: 'alice',
        password: 'Password!123',
      });

      request.reply({
        statusCode: 200,
        body: {
          token: 'jwt-token',
          user: authenticatedUser,
        },
      });
    }).as('login');

    cy.intercept('GET', '**/api/feed?sort=desc', (request) => {
      expect(request.headers.authorization).to.equal('Bearer jwt-token');
      request.reply({
        statusCode: 200,
        body: [],
      });
    }).as('feed');

    cy.visitApp('/login');

    cy.getBySel('login-identifier').closest('mat-form-field').click();
    cy.getBySel('login-identifier').type('alice');
    cy.getBySel('login-password').closest('mat-form-field').click();
    cy.getBySel('login-password').type('Password!123');
    cy.getBySel('login-submit').click();

    cy.wait('@login');
    cy.wait('@feed');
    cy.url().should('include', '/feed');
    cy.getBySel('feed-empty').should('be.visible');
  });

  it('registers a user then lands on the feed', () => {
    cy.intercept('POST', '**/api/auth/register', (request) => {
      expect(request.body).to.deep.equal({
        username: 'new-user',
        email: 'new-user@test.dev',
        password: 'Password!123',
      });

      request.reply({
        statusCode: 200,
        body: {
          token: 'jwt-token',
          user: {
            ...authenticatedUser,
            username: 'new-user',
            email: 'new-user@test.dev',
          },
        },
      });
    }).as('register');

    cy.intercept('GET', '**/api/feed?sort=desc', {
      statusCode: 200,
      body: [],
    }).as('feed');

    cy.visitApp('/register');

    cy.getBySel('register-username').closest('mat-form-field').click();
    cy.getBySel('register-username').type('new-user');
    cy.getBySel('register-email').closest('mat-form-field').click();
    cy.getBySel('register-email').type('new-user@test.dev');
    cy.getBySel('register-password').closest('mat-form-field').click();
    cy.getBySel('register-password').type('Password!123');
    cy.getBySel('register-submit').click();

    cy.wait('@register');
    cy.wait('@feed');
    cy.url().should('include', '/feed');
  });
});
