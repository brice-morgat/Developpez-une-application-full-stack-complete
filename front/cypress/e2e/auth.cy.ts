describe('Authentication flow', () => {
  it('should show backend error toast when login fails', () => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 401,
      body: {
        message: 'Identifiants invalides.',
      },
    }).as('login');

    cy.visit('/login');

    cy.get('input[formcontrolname="identifier"]').type('alice');
    cy.get('input[formcontrolname="password"]').type('wrong-password');
    cy.contains('button', 'Se connecter').click();

    cy.wait('@login');
    cy.contains('Identifiants invalides.').should('be.visible');
  });

  it('should navigate to feed after successful login', () => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: {
        token: 'jwt-token',
        user: { id: 1, email: 'alice@test.com', username: 'alice' },
      },
    }).as('login');

    cy.intercept('GET', '**/api/feed**', {
      statusCode: 200,
      body: [],
    }).as('feed');

    cy.visit('/login');

    cy.get('input[formcontrolname="identifier"]').type('alice');
    cy.get('input[formcontrolname="password"]').type('password123');
    cy.contains('button', 'Se connecter').click();

    cy.wait('@login');
    cy.wait('@feed');
    cy.url().should('include', '/feed');
    cy.contains('Créer un article').should('be.visible');
  });
});
