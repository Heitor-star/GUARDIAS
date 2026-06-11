package com.hegaf.guardians.mapper;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.hegaf.guardians.dto.UsuarioCadastroDTO;
import com.hegaf.guardians.dto.UsuarioPerfilDTO;
import com.hegaf.guardians.model.Usuario;



/**
 * Mapper estático entre DTOs e entidades.
 * Sem dependências de framework — puro Java.
 */
public class UsuarioMapper {

    private UsuarioMapper() {}

    // ── UsuarioCadastroDTO → Usuario (novo cadastro) ───────────

    public static Usuario toEntidade(UsuarioCadastroDTO dto) {
        Usuario u = new Usuario();
        u.setNome(dto.getNome());
        u.setEmail(dto.getEmail());
        u.setCpf(dto.getCpf());
        u.setTelefone(dto.getTelefone());
        u.setSenha(dto.getSenha());             // encode feito no Service
        u.setTipo(dto.getTipo() != null && !dto.getTipo().isBlank()
                ? dto.getTipo() : "ROLE_USER");
        u.setAreaAtuacao(dto.getAreaAtuacao());
        u.setEmpresa(dto.getEmpresa());
        u.setDataNascimento(parseData(dto.getDataNascimento()));
        return u;
    }

    // Alias para compatibilidade com código que chama toEntity()
    public static Usuario toEntity(UsuarioCadastroDTO dto) {
        return toEntidade(dto);
    }

    // ── UsuarioPerfilDTO → atualiza Usuario existente ──────────

    public static void atualizarEntidade(Usuario u, UsuarioPerfilDTO dto) {
        if (dto.getNome() != null && !dto.getNome().isBlank())
            u.setNome(dto.getNome());
        if (dto.getTelefone() != null && !dto.getTelefone().isBlank())
            u.setTelefone(dto.getTelefone());
        u.setAreaAtuacao(dto.getAreaAtuacao());
        u.setEmpresa(dto.getEmpresa());
        u.setDataNascimento(parseData(dto.getDataNascimento()));
        // CPF, email e tipo NAO sao alterados pelo perfil
    }

    // ── Usuario → UsuarioPerfilDTO (leitura em views/APIs) ─────

    public static UsuarioPerfilDTO toPerfilDTO(Usuario u) {
        UsuarioPerfilDTO dto = new UsuarioPerfilDTO();
        dto.setId(u.getId());
        dto.setNome(u.getNome());
        dto.setEmail(u.getEmail());
        dto.setCpf(u.getCpf());
        dto.setTelefone(u.getTelefone());
        dto.setAreaAtuacao(u.getAreaAtuacao());
        dto.setEmpresa(u.getEmpresa());
        dto.setTipo(u.getTipo());
        dto.setFotoPerfil(u.getFotoPerfil());
        if (u.getDataNascimento() != null)
            dto.setDataNascimento(u.getDataNascimento().toString());
        return dto;
    }

    // ── Utilitario ─────────────────────────────────────────────

    private static LocalDate parseData(String data) {
        if (data == null || data.isBlank()) return null;
        try {
            return LocalDate.parse(data);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}