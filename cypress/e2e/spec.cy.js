describe('Página de Contato', () => {

  it('deve abrir a página de contato', () => {

      cy.visit('http://127.0.0.1:8080/contato')

      cy.url().should('include', '/contato')

  })

})