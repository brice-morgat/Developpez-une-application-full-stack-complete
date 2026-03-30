describe('Public pages', () => {
  it('should display welcome page and navigate to login/register', () => {
    cy.visit('/');

    cy.contains('button', 'Se connecter').should('be.visible').click();
    cy.url().should('include', '/login');

    cy.visit('/');
    cy.contains('button', "S'inscrire").should('be.visible').click();
    cy.url().should('include', '/register');
  });
});
