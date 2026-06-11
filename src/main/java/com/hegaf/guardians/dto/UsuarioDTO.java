package com.hegaf.guardians.dto;

import java.time.LocalDate;

import com.hegaf.guardians.model.Usuario;

public class UsuarioDTO {
    private Long id;
    private String email;
    private String nome;
    private String cpf;
    private String telefone;
    private LocalDate dataNascimento;
    private String empresa;

    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.email = usuario.getEmail();
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.telefone = usuario.getTelefone();
        this.dataNascimento = usuario.getDataNascimento();
        this.empresa = usuario.getEmpresa();
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getTelefone() { return telefone; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getEmpresa() { return empresa; }
}