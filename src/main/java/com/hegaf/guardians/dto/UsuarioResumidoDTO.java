package com.hegaf.guardians.dto;

public class UsuarioResumidoDTO {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private String tipo;

    public UsuarioResumidoDTO() {}

    public UsuarioResumidoDTO(Long id, String nome, String email,
                               String cpf, String telefone, String tipo) {
        this.id       = id;
        this.nome     = nome;
        this.email    = email;
        this.cpf      = cpf;
        this.telefone = telefone;
        this.tipo     = tipo;
    }

    public Long   getId()                    { return id; }
    public void   setId(Long id)             { this.id = id; }

    public String getNome()                  { return nome; }
    public void   setNome(String nome)       { this.nome = nome; }

    public String getEmail()                 { return email; }
    public void   setEmail(String email)     { this.email = email; }

    public String getCpf()                   { return cpf; }
    public void   setCpf(String cpf)         { this.cpf = cpf; }

    public String getTelefone()              { return telefone; }
    public void   setTelefone(String t)      { this.telefone = t; }

    public String getTipo()                  { return tipo; }
    public void   setTipo(String tipo)       { this.tipo = tipo; }
}