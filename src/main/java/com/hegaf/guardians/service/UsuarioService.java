package com.hegaf.guardians.service;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hegaf.guardians.dto.UsuarioCadastroDTO;
import com.hegaf.guardians.dto.UsuarioPerfilDTO;
import com.hegaf.guardians.dto.UsuarioSenhaDTO;
import com.hegaf.guardians.mapper.UsuarioMapper;
import com.hegaf.guardians.model.Endereco;
import com.hegaf.guardians.model.Usuario;
import com.hegaf.guardians.repository.EnderecoRepository;
import com.hegaf.guardians.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EnderecoRepository enderecoRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public void cadastrar(UsuarioCadastroDTO dto, Endereco endereco) {
        // Valida os requisitos de força da senha
        validarSenha(dto.getSenha());

        // CORREÇÃO: Valida confirmação apenas se ela for preenchida (ex: tela pública)
        // Se estiver nula ou vazia (tela admin/operador), ele ignora a comparação
        if (dto.getConfirmarSenha() != null && !dto.getConfirmarSenha().isBlank()) {
            if (!dto.getSenha().equals(dto.getConfirmarSenha())) {
                throw new IllegalArgumentException("As senhas não coincidem.");
            }
        }

        if (usuarioRepository.findByEmail(dto.getEmail()) != null)
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");

        if (usuarioRepository.existsByCpf(dto.getCpf()))
            throw new IllegalArgumentException("Este CPF já está cadastrado.");

        Usuario usuario = UsuarioMapper.toEntidade(dto);
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        
        // Garante que o tipo de usuário (Role) seja salvo corretamente
        if (dto.getTipo() != null) {
            usuario.setTipo(dto.getTipo());
        }

        usuarioRepository.save(usuario);

        if (endereco != null && endereco.getCep() != null && !endereco.getCep().isBlank()) {
            endereco.setUsuario(usuario);
            enderecoRepository.save(endereco);
        }
    }

    @Transactional
    public void atualizarPerfil(String email, UsuarioPerfilDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) throw new IllegalArgumentException("Usuário não encontrado.");
        UsuarioMapper.atualizarEntidade(usuario, dto);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void alterarSenha(String email, UsuarioSenhaDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) throw new IllegalArgumentException("Usuário não encontrado.");
        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha()))
            throw new IllegalArgumentException("Senha atual incorreta.");
        if (!dto.getNovaSenha().equals(dto.getConfirmarSenha()))
            throw new IllegalArgumentException("As senhas não coincidem.");
        validarSenha(dto.getNovaSenha());
        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void atualizarFoto(String email, MultipartFile foto) throws IOException {
        if (foto == null || foto.isEmpty()) throw new IllegalArgumentException("Nenhuma foto selecionada.");
        String tipo = foto.getContentType();
        if (tipo == null || !tipo.startsWith("image/")) throw new IllegalArgumentException("Arquivo deve ser uma imagem.");
        if (foto.getSize() > 2 * 1024 * 1024) throw new IllegalArgumentException("A foto deve ter no máximo 2MB.");
        String ext = foto.getOriginalFilename() != null ? foto.getOriginalFilename().substring(foto.getOriginalFilename().lastIndexOf('.')) : ".jpg";
        String nomeArquivo = UUID.randomUUID() + ext;
        Path destino = Paths.get("src/main/resources/static/uploads/fotos/" + nomeArquivo);
        Files.createDirectories(destino.getParent());
        Files.copy(foto.getInputStream(), destino);
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) throw new IllegalArgumentException("Usuário não encontrado.");
        usuario.setFotoPerfil(nomeArquivo);
        usuarioRepository.save(usuario);
    }

    public UsuarioPerfilDTO buscarPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) throw new IllegalArgumentException("Usuário não encontrado.");
        return UsuarioMapper.toPerfilDTO(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.isBlank()) throw new IllegalArgumentException("A senha é obrigatória.");
        if (senha.length() < 8) throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres.");
        if (!senha.matches(".*[A-Z].*")) throw new IllegalArgumentException("A senha deve conter pelo menos uma letra maiúscula.");
        if (!senha.matches(".*[0-9].*")) throw new IllegalArgumentException("A senha deve conter pelo menos um número.");
        if (!senha.matches(".*[!@#$%^&*(),.?\":{}|<>\\-_=+\\[\\]\\\\/].*")) throw new IllegalArgumentException("A senha deve conter pelo menos um caractere especial.");
    }
}