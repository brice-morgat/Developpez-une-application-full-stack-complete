import { topicsResponse } from '../support/mock-data';

describe('Profile management', () => {
  it('updates the profile and shows a success message', () => {
    cy.intercept('GET', '**/api/users/me', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'alice',
        email: 'alice@mdd.dev',
        subscriptions: [2],
      },
    }).as('getMe');

    cy.intercept('GET', '**/api/topics', {
      statusCode: 200,
      body: topicsResponse,
    }).as('getTopics');

    cy.intercept('PUT', '**/api/users/me', (request) => {
      expect(request.body).to.deep.equal({
        username: 'alice-updated',
        email: 'alice-updated@mdd.dev',
        password: 'BetterPass!123',
      });

      request.reply({
        statusCode: 200,
        body: {
          id: 1,
          username: 'alice-updated',
          email: 'alice-updated@mdd.dev',
          subscriptions: [2],
        },
      });
    }).as('updateMe');

    cy.visitApp('/profile', { authenticated: true });

    cy.wait('@getMe');
    cy.wait('@getTopics');
    cy.getBySel('profile-username').clear().type('alice-updated');
    cy.getBySel('profile-email').clear().type('alice-updated@mdd.dev');
    cy.getBySel('profile-password').closest('mat-form-field').click();
    cy.getBySel('profile-password').type('BetterPass!123');
    cy.getBySel('profile-save').click();

    cy.wait('@updateMe');
    cy.getBySel('profile-success').should('contain', 'Profil mis à jour.');
  });

  it('removes a subscription and logs out from the header', () => {
    cy.intercept('GET', '**/api/users/me', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'alice',
        email: 'alice@mdd.dev',
        subscriptions: [2],
      },
    }).as('getMe');

    cy.intercept('GET', '**/api/topics', {
      statusCode: 200,
      body: topicsResponse,
    }).as('getTopics');

    cy.intercept('DELETE', '**/api/topics/2/subscribe', {
      statusCode: 200,
      body: {},
    }).as('unsubscribeTopic');

    cy.visitApp('/profile', { authenticated: true });

    cy.wait('@getMe');
    cy.wait('@getTopics');
    cy.getBySel('unsubscribe-topic-2').click();
    cy.wait('@unsubscribeTopic');
    cy.getBySel('profile-no-subscriptions').should('be.visible');

    cy.getBySel('logout-button').click();
    cy.url().should('include', '/login');
  });
});
