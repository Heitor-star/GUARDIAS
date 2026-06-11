package com.hegaf.guardians.service;

import com.hegaf.guardians.dto.UsuarioCadastroDTO;
import com.hegaf.guardians.dto.UsuarioPerfilDTO;
import com.hegaf.guardians.dto.UsuarioSenhaDTO;
import com.hegaf.guardians.mapper.UsuarioMapper;
import com.hegaf.guardians.model.Endereco;
import com.hegaf.guardians.model.Usuario;
import com.hegaf.guardians.repository.EnderecoRepository;
import com.hegaf.guardians.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes de Unidade para UsuarioService.
 *
 * Cada método testa um único comportamento isolado.
 * As dependências (repositories, encoder) são substituídas por mocks do Mockito,
 * ou seja, nenhum banco de dados ou servidor é acessado durante estes testes.
 */
@ExtendWith(MockitoExtension.class) // Habilita o Mockito no JUnit 5
class UsuarioServiceTest {

    // ── Mocks das dependências ──────────────────────────────────────────────────
    // O Mockito cria versões "falsas" dessas interfaces para isolar o Service.

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // ── Classe sendo testada ────────────────────────────────────────────────────
    // @InjectMocks injeta os mocks acima automaticamente no construtor/fields.

    @InjectMocks
    private UsuarioService usuarioService;

    // ── Objetos reutilizados nos testes ─────────────────────────────────────────

    private UsuarioCadastroDTO dtoCadastroValido;
    private Usuario usuarioExistente;

    @BeforeEach
    void setUp() {
        // DTO com dados válidos para cadastro
        dtoCadastroValido = new UsuarioCadastroDTO();
        dtoCadastroValido.setNome("Heitor Teste");
        dtoCadastroValido.setEmail("heitor@email.com");
        dtoCadastroValido.setCpf("123.456.789-00");
        dtoCadastroValido.setSenha("Senha@123");
        dtoCadastroValido.setConfirmarSenha("Senha@123");

        // Usuário que simula um registro já existente no banco
        usuarioExistente = new Usuario();
        usuarioExistente.setEmail("heitor@email.com");
        usuarioExistente.setSenha("$2a$10$hashBCryptFicticio");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TESTES DE cadastrar()
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve cadastrar usuário com sucesso quando os dados são válidos")
    void deveCadastrarUsuarioComSucesso() {
        // ARRANGE – configura o comportamento dos mocks
        when(usuarioRepository.findByEmail(anyString())).thenReturn(null);    // e-mail livre
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);   // CPF livre
        when(passwordEncoder.encode(anyString())).thenReturn("hashFicticio");

        Usuario usuarioMapeado = new Usuario();
        usuarioMapeado.setEmail(dtoCadastroValido.getEmail());

        try (MockedStatic<UsuarioMapper> mapperMock = mockStatic(UsuarioMapper.class)) {
            mapperMock.when(() -> UsuarioMapper.toEntidade(any())).thenReturn(usuarioMapeado);

            // ACT – executa o método sendo testado
            assertDoesNotThrow(() -> usuarioService.cadastrar(dtoCadastroValido, null));

            // ASSERT – verifica que o save foi chamado exatamente uma vez
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }
    }

    @Test
    @DisplayName("Deve lançar exceção quando as senhas não coincidem")
    void deveLancarExcecaoQuandoSenhasNaoCoincidem() {
        // ARRANGE
        dtoCadastroValido.setConfirmarSenha("SenhaErrada@1");

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.cadastrar(dtoCadastroValido, null)
        );

        assertEquals("As senhas não coincidem.", ex.getMessage());

        // Garante que o save NUNCA foi chamado (nenhum dado salvo)
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o e-mail já está cadastrado")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // ARRANGE – simula e-mail já existente no banco
        when(usuarioRepository.findByEmail(dtoCadastroValido.getEmail()))
            .thenReturn(usuarioExistente);

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.cadastrar(dtoCadastroValido, null)
        );

        assertEquals("Este e-mail já está cadastrado.", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o CPF já está cadastrado")
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        // ARRANGE
        when(usuarioRepository.findByEmail(anyString())).thenReturn(null);
        when(usuarioRepository.existsByCpf(dtoCadastroValido.getCpf())).thenReturn(true);

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.cadastrar(dtoCadastroValido, null)
        );

        assertEquals("Este CPF já está cadastrado.", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha não tem letra maiúscula")
    void deveLancarExcecaoQuandoSenhaSemMaiuscula() {
        dtoCadastroValido.setSenha("senha@123");
        dtoCadastroValido.setConfirmarSenha("senha@123");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.cadastrar(dtoCadastroValido, null)
        );

        assertEquals("A senha deve conter pelo menos uma letra maiúscula.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha não tem caractere especial")
    void deveLancarExcecaoQuandoSenhaSemCaractereEspecial() {
        dtoCadastroValido.setSenha("Senha1234");
        dtoCadastroValido.setConfirmarSenha("Senha1234");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.cadastrar(dtoCadastroValido, null)
        );

        assertEquals("A senha deve conter pelo menos um caractere especial.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve salvar o endereço quando ele é fornecido e tem CEP válido")
    void deveSalvarEnderecoQuandoFornecido() {
        // ARRANGE
        when(usuarioRepository.findByEmail(anyString())).thenReturn(null);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");

        Endereco endereco = new Endereco();
        endereco.setCep("38400-000");

        Usuario usuarioMapeado = new Usuario();
        usuarioMapeado.setEmail(dtoCadastroValido.getEmail());

        try (MockedStatic<UsuarioMapper> mapperMock = mockStatic(UsuarioMapper.class)) {
            mapperMock.when(() -> UsuarioMapper.toEntidade(any())).thenReturn(usuarioMapeado);

            // ACT
            assertDoesNotThrow(() -> usuarioService.cadastrar(dtoCadastroValido, endereco));

            // ASSERT – o endereço deve ter sido salvo também
            verify(enderecoRepository, times(1)).save(any(Endereco.class));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TESTES DE alterarSenha()
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve alterar a senha com sucesso quando os dados são válidos")
    void deveAlterarSenhaComSucesso() {
        // ARRANGE
        UsuarioSenhaDTO dto = new UsuarioSenhaDTO();
        dto.setSenhaAtual("SenhaAntiga@1");
        dto.setNovaSenha("NovaSenha@2");
        dto.setConfirmarSenha("NovaSenha@2");

        when(usuarioRepository.findByEmail("heitor@email.com")).thenReturn(usuarioExistente);
        when(passwordEncoder.matches("SenhaAntiga@1", usuarioExistente.getSenha())).thenReturn(true);
        when(passwordEncoder.encode("NovaSenha@2")).thenReturn("novoHashBCrypt");

        // ACT & ASSERT
        assertDoesNotThrow(() -> usuarioService.alterarSenha("heitor@email.com", dto));
        verify(usuarioRepository, times(1)).save(usuarioExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha atual está incorreta")
    void deveLancarExcecaoQuandoSenhaAtualIncorreta() {
        // ARRANGE
        UsuarioSenhaDTO dto = new UsuarioSenhaDTO();
        dto.setSenhaAtual("SenhaErrada@1");
        dto.setNovaSenha("NovaSenha@2");
        dto.setConfirmarSenha("NovaSenha@2");

        when(usuarioRepository.findByEmail("heitor@email.com")).thenReturn(usuarioExistente);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.alterarSenha("heitor@email.com", dto)
        );

        assertEquals("Senha atual incorreta.", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  TESTES DE buscarPerfil()
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Deve retornar o perfil do usuário quando o e-mail existe")
    void deveRetornarPerfilQuandoEmailExiste() {
        // ARRANGE
        UsuarioPerfilDTO perfilEsperado = new UsuarioPerfilDTO();
        perfilEsperado.setNome("Heitor Teste");

        when(usuarioRepository.findByEmail("heitor@email.com")).thenReturn(usuarioExistente);

        try (MockedStatic<UsuarioMapper> mapperMock = mockStatic(UsuarioMapper.class)) {
            mapperMock.when(() -> UsuarioMapper.toPerfilDTO(usuarioExistente)).thenReturn(perfilEsperado);

            // ACT
            UsuarioPerfilDTO resultado = usuarioService.buscarPerfil("heitor@email.com");

            // ASSERT
            assertNotNull(resultado);
            assertEquals("Heitor Teste", resultado.getNome());
        }
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar perfil de e-mail inexistente")
    void deveLancarExcecaoAoBuscarPerfilInexistente() {
        // ARRANGE
        when(usuarioRepository.findByEmail("naoexiste@email.com")).thenReturn(null);

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.buscarPerfil("naoexiste@email.com")
        );

        assertEquals("Usuário não encontrado.", ex.getMessage());
    }
}