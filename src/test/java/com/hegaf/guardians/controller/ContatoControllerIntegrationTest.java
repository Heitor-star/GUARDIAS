package com.hegaf.guardians.controller;

import com.hegaf.guardians.repository.ContatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de Integração para ContatoController.
 *
 * @SpringBootTest sobe o contexto completo do Spring (Controller + Service + Repository).
 * @AutoConfigureMockMvc permite simular requisições HTTP sem precisar de um servidor real.
 * @ActiveProfiles("test") usa o application-test.properties com banco H2 em memória.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContatoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContatoRepository contatoRepository;

    @BeforeEach
    void limparBanco() {
        // Garante que cada teste começa com o banco limpo
        contatoRepository.deleteAll();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  GET /contato — exibir formulário
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /contato deve retornar status 200 e exibir o formulário")
    void deveExibirFormularioDeContato() throws Exception {
        mockMvc.perform(get("/contato"))
                .andExpect(status().isOk())
                .andExpect(view().name("contato"))
                .andExpect(model().attributeExists("contatoDTO"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  POST /contato/enviar — envio válido
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /contato/enviar deve salvar a mensagem e redirecionar com sucesso")
    void deveEnviarContatoComSucesso() throws Exception {
        mockMvc.perform(post("/contato/enviar")
                        .with(csrf())                          // necessário com Spring Security
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nome",     "Heitor Teste")
                        .param("email",    "heitor@email.com")
                        .param("telefone", "34999999999")
                        .param("assunto",  "Dúvida")
                        .param("mensagem", "Mensagem de teste de integração"))
                .andExpect(status().is3xxRedirection())        // espera redirect
                .andExpect(redirectedUrlPattern("/contato*")); // redireciona para /contato?enviado

        // Verifica que a mensagem foi de fato salva no banco H2
        assertThat(contatoRepository.count()).isEqualTo(1);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  POST /contato/enviar — dados inválidos (campos obrigatórios em branco)
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /contato/enviar deve retornar ao formulário quando os dados são inválidos")
    void deveRetornarFormularioQuandoDadosInvalidos() throws Exception {
        mockMvc.perform(post("/contato/enviar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nome",     "")   // nome em branco — inválido
                        .param("email",    "")   // email em branco — inválido
                        .param("assunto",  "")
                        .param("mensagem", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("contato")); // permanece na página do formulário

        // Nada deve ter sido salvo no banco
        assertThat(contatoRepository.count()).isEqualTo(0);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  POST /contato/enviar — múltiplos envios salvam registros separados
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Dois envios válidos devem gerar dois registros no banco")
    void doisEnviosDevemGerarDoisRegistros() throws Exception {
        // Primeiro envio
        mockMvc.perform(post("/contato/enviar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nome",     "Usuário Um")
                        .param("email",    "um@email.com")
                        .param("telefone", "34911111111")
                        .param("assunto",  "Assunto 1")
                        .param("mensagem", "Primeira mensagem"))
                .andExpect(status().is3xxRedirection());

        // Segundo envio
        mockMvc.perform(post("/contato/enviar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nome",     "Usuário Dois")
                        .param("email",    "dois@email.com")
                        .param("telefone", "34922222222")
                        .param("assunto",  "Assunto 2")
                        .param("mensagem", "Segunda mensagem"))
                .andExpect(status().is3xxRedirection());

        // Verifica que os dois foram salvos
        assertThat(contatoRepository.count()).isEqualTo(2);
    }
}