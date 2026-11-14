package com.carbonfootprint.model;

import java.time.LocalDate;

/**
 * Entidade Usuario — persiste no banco de dados.
 * Demonstra ENCAPSULAMENTO: todos os atributos são privados,
 * acessados apenas por getters/setters.
 */
public class Usuario {

    private int       id;
    private String    nome;
    private String    email;
    private String    cidade;
    private LocalDate dataCadastro;

    // ── Construtor completo ───────────────────────────────────────────────────
    public Usuario(int id, String nome, String email, String cidade, LocalDate dataCadastro) {
        this.id           = id;
        this.nome         = nome;
        this.email        = email;
        this.cidade       = cidade;
        this.dataCadastro = dataCadastro;
    }

    // ── Construtor sem ID (novo usuário) ──────────────────────────────────────
    public Usuario(String nome, String email, String cidade) {
        this(0, nome, email, cidade, LocalDate.now());
    }

    // ── Getters / Setters (ENCAPSULAMENTO) ────────────────────────────────────
    public int       getId()           { return id; }
    public String    getNome()         { return nome; }
    public String    getEmail()        { return email; }
    public String    getCidade()       { return cidade; }
    public LocalDate getDataCadastro() { return dataCadastro; }

    public void setId(int id)                     { this.id           = id; }
    public void setNome(String nome)              { this.nome         = nome; }
    public void setEmail(String email)            { this.email        = email; }
    public void setCidade(String cidade)          { this.cidade       = cidade; }
    public void setDataCadastro(LocalDate data)   { this.dataCadastro = data; }

    @Override
    public String toString() {
        return String.format("Usuario{id=%d, nome='%s', email='%s', cidade='%s', cadastro=%s}",
                id, nome, email, cidade, dataCadastro);
    }
}
