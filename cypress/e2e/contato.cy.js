describe('Página de Contato', () => {

    it('deve enviar uma mensagem com sucesso', () => {

        cy.visit('http://127.0.0.1:8080/contato')

        // Nome
        cy.get('#nome')
            .type('Heitor Teste')

        // Telefone
        cy.get('#telefone')
            .type('34999999999')

        // Email
        cy.get('#email')
            .type('heitor@email.com')

        // Assunto
        cy.get('#assunto')
            .select('Duvida')

        // Mensagem
        cy.get('#mensagem')
            .type('Mensagem enviada automaticamente pelo Cypress.')

        // Enviar
        cy.get('#btnEnviar')
            .click()

        // Verifica se a URL recebeu ?enviado
        cy.url()
            .should('include', 'enviado')

        // Verifica a mensagem de sucesso
        cy.contains('Mensagem enviada com sucesso')
            .should('be.visible')

    })

})