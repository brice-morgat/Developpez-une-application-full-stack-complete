describe('Public pages', () => {
  it('navigates from welcome page to login and register', () => {
    cy.visitApp('/');

    cy.getBySel('welcome-login').click();
    cy.url().should('include', '/login');

    cy.visitApp('/');
    cy.getBySel('welcome-register').click();
    cy.url().should('include', '/register');
  });

  ['/feed', '/topics', '/create-post', '/profile'].forEach((route) => {
    it(`redirects unauthenticated users from ${route} to login`, () => {
      cy.visitApp(route);
      cy.url().should('include', '/login');
    });
  });
});
