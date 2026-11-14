package com.carbonfootprint.dao;

import com.carbonfootprint.database.DatabaseConnection;
import com.carbonfootprint.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CRUD completo para a entidade Usuario.
 * Implementa GenericDAO — demonstra uso de INTERFACE e POLIMORFISMO.
 */
public class UsuarioDAO implements GenericDAO<Usuario, Integer> {

    private final Connection conn;

    public UsuarioDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    public Usuario salvar(Usuario u) {
        String sql = "INSERT INTO usuarios (nome, email, cidade, data_cadastro) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id"})) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getCidade());
            ps.setDate(4, java.sql.Date.valueOf(u.getDataCadastro()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                u.setId(keys.getInt(1));
            }
            System.out.println("✅ Usuário salvo: " + u.getNome() + " (ID=" + u.getId() + ")");
            return u;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário: " + e.getMessage(), e);
        }
    }

    // ── READ por ID ───────────────────────────────────────────────────────────
    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por ID: " + e.getMessage(), e);
        }
    }

    // ── READ por e-mail ───────────────────────────────────────────────────────
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por e-mail: " + e.getMessage(), e);
        }
    }

    // ── READ todos ────────────────────────────────────────────────────────────
    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Override
    public boolean atualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nome=?, email=?, cidade=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getCidade());
            ps.setInt(4, u.getId());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Usuário atualizado: ID=" + u.getId());
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Override
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Usuário removido: ID=" + id);
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar usuário: " + e.getMessage(), e);
        }
    }

    // ── Mapeamento ResultSet → objeto ─────────────────────────────────────────
    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("cidade"),
            rs.getDate("data_cadastro").toLocalDate()
        );
    }
}
