package com.hegaf.guardians.dto;

public class UsuarioPerfilDTO {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private String dataNascimento;
    private String areaAtuacao;
    private String empresa;
    private String tipo;
    private String fotoPerfil;

    public UsuarioPerfilDTO() {}

    public Long getId()                            { return id; }
    public void setId(Long id)                     { this.id = id; }

    public String getNome()                        { return nome; }
    public void   setNome(String nome)             { this.nome = nome; }

    public String getEmail()                       { return email; }
    public void   setEmail(String email)           { this.email = email; }

    public String getCpf()                         { return cpf; }
    public void   setCpf(String cpf)               { this.cpf = cpf; }

    public String getTelefone()                    { return telefone; }
    public void   setTelefone(String telefone)     { this.telefone = telefone; }

    public String getDataNascimento()              { return dataNascimento; }
    public void   setDataNascimento(String d)      { this.dataNascimento = d; }

    public String getAreaAtuacao()                 { return areaAtuacao; }
    public void   setAreaAtuacao(String a)         { this.areaAtuacao = a; }

    public String getEmpresa()                     { return empresa; }
    public void   setEmpresa(String empresa)       { this.empresa = empresa; }

    public String getTipo()                        { return tipo; }
    public void   setTipo(String tipo)             { this.tipo = tipo; }

    public String getFotoPerfil()                  { return fotoPerfil; }
    public void   setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
}