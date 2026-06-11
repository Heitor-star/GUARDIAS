package com.hegaf.guardians.model;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "acesso")
public class Acesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identificador; 
    private LocalDate dataAcesso;

    public Acesso() {}

    public Acesso(String identificador, LocalDate dataAcesso) {
        this.identificador = identificador;
        this.dataAcesso = dataAcesso;
    }

    public Long getId() { return id; }
    public String getIdentificador() { return identificador; }
    public LocalDate getDataAcesso() { return dataAcesso; }
}