import { ascendingFeed, descendingFeed, postDetailResponse, topicsResponse } from '../support/mock-data';

describe('Feed and posts', () => {
  it('loads the feed, toggles sort, and opens a post detail page', () => {
    cy.intercept('GET', '**/api/feed?sort=desc', {
      statusCode: 200,
      body: descendingFeed,
    }).as('feedDesc');

    cy.intercept('GET', '**/api/feed?sort=asc', {
      statusCode: 200,
      body: ascendingFeed,
    }).as('feedAsc');

    cy.intercept('GET', '**/api/posts/10', {
      statusCode: 200,
      body: postDetailResponse,
    }).as('getPost');

    cy.visitApp('/feed', { authenticated: true });

    cy.wait('@feedDesc');
    cy.getBySel('post-card-20').should('be.visible');
    cy.get('[data-cy="post-card-title"]').first().should('contain', descendingFeed[0].title);

    cy.getBySel('feed-sort-button').click();
    cy.wait('@feedAsc');
    cy.get('[data-cy="post-card-title"]').first().should('contain', ascendingFeed[0].title);

    cy.getBySel('post-card-10').click();
    cy.wait('@getPost');
    cy.url().should('include', '/post/10');
    cy.getBySel('post-detail-title').should('contain', postDetailResponse.title);
    cy.getBySel('comment-101').should('contain', 'Très clair, merci.');
  });

  it('creates a post from the dedicated page', () => {
    cy.intercept('GET', '**/api/topics', {
      statusCode: 200,
      body: topicsResponse,
    }).as('getTopics');

    cy.intercept('POST', '**/api/posts', (request) => {
      expect(request.headers.authorization).to.equal('Bearer jwt-token');
      expect(request.body).to.deep.equal({
        topicId: 2,
        title: 'Créer une suite Cypress durable',
        content: 'On sécurise les parcours critiques avec des commandes custom et des assertions API.',
      });

      request.reply({
        statusCode: 200,
        body: {
          id: 99,
          title: request.body.title,
          content: request.body.content,
          author: { id: 1, username: 'alice' },
          topic: { id: 2, name: 'Angular' },
          createdAt: '2026-03-31T12:00:00Z',
        },
      });
    }).as('createPost');

    cy.intercept('GET', '**/api/posts/99', {
      statusCode: 200,
      body: {
        id: 99,
        title: 'Créer une suite Cypress durable',
        content: 'On sécurise les parcours critiques avec des commandes custom et des assertions API.',
        author: { id: 1, username: 'alice' },
        topic: { id: 2, name: 'Angular' },
        createdAt: '2026-03-31T12:00:00Z',
        comments: [],
      },
    }).as('getCreatedPost');

    cy.visitApp('/create-post', { authenticated: true });

    cy.wait('@getTopics');
    cy.selectMatOption('create-post-topic', 'Angular');
    cy.getBySel('create-post-title').type('Créer une suite Cypress durable');
    cy.getBySel('create-post-content').type(
      'On sécurise les parcours critiques avec des commandes custom et des assertions API.'
    );
    cy.getBySel('create-post-submit').click();

    cy.wait('@createPost');
    cy.wait('@getCreatedPost');
    cy.url().should('include', '/post/99');
    cy.getBySel('post-detail-title').should('contain', 'Créer une suite Cypress durable');
  });

  it('adds a comment on a post detail page', () => {
    cy.intercept('GET', '**/api/posts/10', {
      statusCode: 200,
      body: postDetailResponse,
    }).as('getPost');

    cy.intercept('POST', '**/api/posts/10/comments', (request) => {
      expect(request.body).to.deep.equal({
        content: 'Merci pour ce partage.',
      });

      request.reply({
        statusCode: 200,
        body: {
          id: 202,
          content: 'Merci pour ce partage.',
          author: { id: 1, username: 'alice' },
          createdAt: '2026-03-31T13:00:00Z',
        },
      });
    }).as('createComment');

    cy.visitApp('/post/10', { authenticated: true });

    cy.wait('@getPost');
    cy.getBySel('comment-input').type('Merci pour ce partage.');
    cy.getBySel('comment-submit').click();

    cy.wait('@createComment');
    cy.getBySel('comment-202').should('contain', 'Merci pour ce partage.');
  });
});
