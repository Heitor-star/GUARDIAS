package com.hegaf.guardians.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contato")
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(length = 15)
    private String telefone;

    @Column(nullable = false, length = 80)
    private String assunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(nullable = false)
    private LocalDateTime dataEnvio;

    @Column(nullable = false)
    private boolean lida = false;

    @Column(nullable = false)
    private boolean respondida = false;

    @Column(columnDefinition = "TEXT")
    private String resposta;

    private LocalDateTime dataResposta;

    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }
    public String getNome()                      { return nome; }
    public void   setNome(String nome)           { this.nome = nome; }
    public String getEmail()                     { return email; }
    public void   setEmail(String email)         { this.email = email; }
    public String getTelefone()                  { return telefone; }
    public void   setTelefone(String t)          { this.telefone = t; }
    public String getAssunto()                   { return assunto; }
    public void   setAssunto(String assunto)     { this.assunto = assunto; }
    public String getMensagem()                  { return mensagem; }
    public void   setMensagem(String m)          { this.mensagem = m; }
    public LocalDateTime getDataEnvio()          { return dataEnvio; }
    public void setDataEnvio(LocalDateTime d)    { this.dataEnvio = d; }
    public boolean isLida()                      { return lida; }
    public void    setLida(boolean lida)         { this.lida = lida; }
    public boolean isRespondida()                { return respondida; }
    public void    setRespondida(boolean r)      { this.respondida = r; }
    public String getResposta()                  { return resposta; }
    public void   setResposta(String resposta)   { this.resposta = resposta; }
    public LocalDateTime getDataResposta()       { return dataResposta; }
    public void setDataResposta(LocalDateTime d) { this.dataResposta = d; }
}