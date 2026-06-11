package com.hegaf.guardians.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioCadastroDTO {

    // ── Obrigatorios ───────────────────────────────────────

    @NotBlank(message = "Nome e obrigatorio")
    @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
    private String nome;

    @NotBlank(message = "E-mail e obrigatorio")
    @Email(message = "E-mail invalido")
    private String email;

    // CPF e telefone: validacao de formato feita pelo JS (mascara)
    // @Pattern removido para nao rejeitar antes da mascara aplicar
    @NotBlank(message = "CPF e obrigatorio")
    private String cpf;

    @NotBlank(message = "Telefone e obrigatorio")
    private String telefone;

    @NotBlank(message = "Senha e obrigatoria")
    @Size(min = 8, message = "Senha deve ter no minimo 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>\\-_=+\\[\\]\\\\/]).{8,}$",
        message = "Senha deve conter ao menos 1 maiuscula, 1 numero e 1 caractere especial"
    )
    private String senha;

    private String confirmarSenha;

    private String tipo = "ROLE_USER";

    // ── Opcionais — Perfil ─────────────────────────────────

    private String dataNascimento;
    private String areaAtuacao;

    @Size(max = 120, message = "Empresa deve ter no maximo 120 caracteres")
    private String empresa;

    // ── Opcionais — Endereco ───────────────────────────────

    private String tipoEndereco;

    @Pattern(regexp = "\\d{5}-\\d{3}|", message = "CEP invalido - use 00000-000")
    private String cep;

    private String logradouro;

    @Size(max = 10)
    private String numero;

    private String complemento;
    private String bairro;
    private String cidade;

    @Size(max = 2, message = "Informe a UF com 2 letras")
    private String estado;

    public UsuarioCadastroDTO() {}

    // ── Getters e Setters ──────────────────────────────────

    public String getNome()                        { return nome; }
    public void   setNome(String nome)             { this.nome = nome; }

    public String getEmail()                       { return email; }
    public void   setEmail(String email)           { this.email = email; }

    public String getCpf()                         { return cpf; }
    public void   setCpf(String cpf)               { this.cpf = cpf; }

    public String getTelefone()                    { return telefone; }
    public void   setTelefone(String telefone)     { this.telefone = telefone; }

    public String getSenha()                       { return senha; }
    public void   setSenha(String senha)           { this.senha = senha; }

    public String getConfirmarSenha()              { return confirmarSenha; }
    public void   setConfirmarSenha(String s)      { this.confirmarSenha = s; }

    public String getTipo()                        { return tipo; }
    public void   setTipo(String tipo)             { this.tipo = tipo; }

    public String getDataNascimento()              { return dataNascimento; }
    public void   setDataNascimento(String d)      { this.dataNascimento = d; }

    public String getAreaAtuacao()                 { return areaAtuacao; }
    public void   setAreaAtuacao(String a)         { this.areaAtuacao = a; }

    public String getEmpresa()                     { return empresa; }
    public void   setEmpresa(String empresa)       { this.empresa = empresa; }

    public String getTipoEndereco()                { return tipoEndereco; }
    public void   setTipoEndereco(String t)        { this.tipoEndereco = t; }

    public String getCep()                         { return cep; }
    public void   setCep(String cep)               { this.cep = cep; }

    public String getLogradouro()                  { return logradouro; }
    public void   setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero()                      { return numero; }
    public void   setNumero(String numero)         { this.numero = numero; }

    public String getComplemento()                 { return complemento; }
    public void   setComplemento(String c)         { this.complemento = c; }

    public String getBairro()                      { return bairro; }
    public void   setBairro(String bairro)         { this.bairro = bairro; }

    public String getCidade()                      { return cidade; }
    public void   setCidade(String cidade)         { this.cidade = cidade; }

    public String getEstado()                      { return estado; }
    public void   setEstado(String estado)         { this.estado = estado; }
}