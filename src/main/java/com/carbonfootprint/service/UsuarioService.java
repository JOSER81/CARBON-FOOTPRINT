package com.carbonfootprint.service;

import com.carbonfootprint.dao.UsuarioDAO;
import com.carbonfootprint.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Camada de serviço para Usuario.
 * COMPOSIÇÃO: usa UsuarioDAO sem herdar dele.
 * Responsável pelas regras de negócio (validações, etc.).
 */
public class UsuarioService {

    private final UsuarioDAO dao;  // COMPOSIÇÃO — tem um DAO

    public UsuarioService() {
        this.dao = new UsuarioDAO();
    }

    public Usuario cadastrar(String nome, String email, String cidade) {
        validarCampos(nome, email, cidade);

        if (dao.buscarPorEmail(email).isPresent())
            throw new IllegalArgumentException("E-mail já cadastrado: " + email);

        return dao.salvar(new Usuario(nome, email, cidade));
    }

    public Optional<Usuario> buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return dao.buscarPorEmail(email);
    }

    public List<Usuario> listarTodos() {
        return dao.listarTodos();
    }

    public boolean atualizar(Usuario u) {
        validarCampos(u.getNome(), u.getEmail(), u.getCidade());
        return dao.atualizar(u);
    }

    public boolean deletar(int id) {
        return dao.deletar(id);
    }

    private void validarCampos(String nome, String email, String cidade) {
        if (nome  == null || nome.isBlank())  throw new IllegalArgumentException("Nome não pode ser vazio.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("E-mail não pode ser vazio.");
        if (!email.contains("@"))             throw new IllegalArgumentException("E-mail inválido.");
        if (cidade == null || cidade.isBlank()) throw new IllegalArgumentException("Cidade não pode ser vazia.");
    }
}
