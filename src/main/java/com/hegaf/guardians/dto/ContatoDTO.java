package com.hegaf.guardians.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContatoDTO {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 120, message = "Nome muito longo")
    private String nome;

    @NotBlank(message = "E-mail e obrigatorio")
    @Email(message = "E-mail invalido")
    private String email;

    private String telefone;

    @NotBlank(message = "Assunto e obrigatorio")
    @Size(max = 80, message = "Assunto muito longo")
    private String assunto;

    @NotBlank(message = "Mensagem e obrigatoria")
    @Size(min = 10, max = 2000, message = "Mensagem deve ter entre 10 e 2000 caracteres")
    private String mensagem;

    public String getNome()                  { return nome; }
    public void   setNome(String nome)       { this.nome = nome; }
    public String getEmail()                 { return email; }
    public void   setEmail(String email)     { this.email = email; }
    public String getTelefone()              { return telefone; }
    public void   setTelefone(String t)      { this.telefone = t; }
    public String getAssunto()               { return assunto; }
    public void   setAssunto(String a)       { this.assunto = a; }
    public String getMensagem()              { return mensagem; }
    public void   setMensagem(String m)      { this.mensagem = m; }
}