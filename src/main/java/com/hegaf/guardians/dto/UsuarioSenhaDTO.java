package com.hegaf.guardians.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioSenhaDTO {

    @NotBlank(message = "Senha atual e obrigatoria")
    private String senhaAtual;

    @NotBlank(message = "Nova senha e obrigatoria")
    @Size(min = 8, message = "Senha deve ter no minimo 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>\\-_=+\\[\\]\\\\/]).{8,}$",
        message = "Senha deve conter ao menos 1 maiuscula, 1 numero e 1 caractere especial"
    )
    private String novaSenha;

    @NotBlank(message = "Confirmacao e obrigatoria")
    private String confirmarSenha;

    public UsuarioSenhaDTO() {}

    public String getSenhaAtual()                       { return senhaAtual; }
    public void   setSenhaAtual(String senhaAtual)      { this.senhaAtual = senhaAtual; }

    public String getNovaSenha()                        { return novaSenha; }
    public void   setNovaSenha(String novaSenha)        { this.novaSenha = novaSenha; }

    public String getConfirmarSenha()                   { return confirmarSenha; }
    public void   setConfirmarSenha(String confirmarSenha) { this.confirmarSenha = confirmarSenha; }
}