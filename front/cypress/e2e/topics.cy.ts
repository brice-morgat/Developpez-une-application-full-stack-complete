import { topicsResponse } from '../support/mock-data';

describe('Topics management', () => {
  it('loads topics and subscribes to a theme', () => {
    cy.intercept('GET', '**/api/topics', {
      statusCode: 200,
      body: topicsResponse,
    }).as('getTopics');

    cy.intercept('POST', '**/api/topics/1/subscribe', (request) => {
      expect(request.headers.authorization).to.equal('Bearer jwt-token');
      request.reply({
        statusCode: 200,
        body: {},
      });
    }).as('subscribeTopic');

    cy.visitApp('/topics', { authenticated: true });

    cy.wait('@getTopics');
    cy.getBySel('topics-list').should('be.visible');
    cy.getBySel('topic-toggle-1').should('contain', "S'abonner").click();

    cy.wait('@subscribeTopic');
    cy.getBySel('topic-toggle-1').should('contain', 'Déjà abonné');
  });

  it('unsubscribes from an already subscribed theme', () => {
    cy.intercept('GET', '**/api/topics', {
      statusCode: 200,
      body: topicsResponse,
    }).as('getTopics');

    cy.intercept('DELETE', '**/api/topics/2/subscribe', {
      statusCode: 200,
      body: {},
    }).as('unsubscribeTopic');

    cy.visitApp('/topics', { authenticated: true });

    cy.wait('@getTopics');
    cy.getBySel('topic-toggle-2').should('contain', 'Déjà abonné').click();

    cy.wait('@unsubscribeTopic');
    cy.getBySel('topic-toggle-2').should('contain', "S'abonner");
  });
});
