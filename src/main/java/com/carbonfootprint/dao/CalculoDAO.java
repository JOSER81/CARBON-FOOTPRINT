package com.carbonfootprint.dao;

import com.carbonfootprint.database.DatabaseConnection;
import com.carbonfootprint.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CRUD completo para CalculoResult e seus detalhes (calculo_detalhes).
 * Usa transação para garantir consistência entre as duas tabelas.
 */
public class CalculoDAO implements GenericDAO<CalculoResult, Integer> {

    private final Connection  conn;
    private final UsuarioDAO  usuarioDAO;

    public CalculoDAO() {
        this.conn       = DatabaseConnection.getInstance().getConnection();
        this.usuarioDAO = new UsuarioDAO();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    @Override
    public CalculoResult salvar(CalculoResult calculo) {
        String sqlCalculo  = "INSERT INTO calculos (usuario_id, total_emissao, classificacao, dica, data_calculo) VALUES (?,?,?,?,?)";
        String sqlDetalhe  = "INSERT INTO calculo_detalhes (calculo_id, categoria, descricao, valor_kg) VALUES (?,?,?,?)";

        try {
            conn.setAutoCommit(false); // ── início da transação

            // 1. Salva o cálculo principal
            try (PreparedStatement ps = conn.prepareStatement(sqlCalculo, new String[]{"id"})) {
                ps.setInt(1, calculo.getUsuario().getId());
                ps.setDouble(2, calculo.getTotalEmissao());
                ps.setString(3, calculo.getClassificacao());
                ps.setString(4, calculo.getDica());
                ps.setTimestamp(5, Timestamp.valueOf(calculo.getDataCalculo()));
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) calculo.setId(keys.getInt(1));
            }

            // 2. Salva cada fator como detalhe
            try (PreparedStatement ps = conn.prepareStatement(sqlDetalhe)) {
                for (EmissaoFator f : calculo.getFatores()) {
                    ps.setInt(1, calculo.getId());
                    ps.setString(2, f.getNome());
                    ps.setString(3, f.getDescricao());
                    ps.setDouble(4, f.calcular());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit(); // ── commit da transação
            System.out.println("✅ Cálculo salvo: ID=" + calculo.getId()
                    + "  Total=" + calculo.getTotalEmissao() + " kg CO₂");
            return calculo;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* ignora */ }
            throw new RuntimeException("Erro ao salvar cálculo (rollback executado): " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignora */ }
        }
    }

    // ── READ por ID ───────────────────────────────────────────────────────────
    @Override
    public Optional<CalculoResult> buscarPorId(Integer id) {
        String sql = "SELECT * FROM calculos WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CalculoResult c = mapear(rs);
                c.setFatores(buscarDetalhes(c.getId()));
                return Optional.of(c);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cálculo: " + e.getMessage(), e);
        }
    }

    // ── READ todos ────────────────────────────────────────────────────────────
    @Override
    public List<CalculoResult> listarTodos() {
        String sql = "SELECT * FROM calculos ORDER BY data_calculo DESC";
        List<CalculoResult> lista = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cálculos: " + e.getMessage(), e);
        }
    }

    // ── READ por usuário ──────────────────────────────────────────────────────
    public List<CalculoResult> listarPorUsuario(int usuarioId) {
        String sql = "SELECT * FROM calculos WHERE usuario_id=? ORDER BY data_calculo DESC";
        List<CalculoResult> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CalculoResult c = mapear(rs);
                c.setFatores(buscarDetalhes(c.getId()));
                lista.add(c);
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cálculos do usuário: " + e.getMessage(), e);
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    @Override
    public boolean atualizar(CalculoResult calculo) {
        String sql = "UPDATE calculos SET total_emissao=?, classificacao=?, dica=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, calculo.getTotalEmissao());
            ps.setString(2, calculo.getClassificacao());
            ps.setString(3, calculo.getDica());
            ps.setInt(4, calculo.getId());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Cálculo atualizado: ID=" + calculo.getId());
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cálculo: " + e.getMessage(), e);
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    @Override
    public boolean deletar(Integer id) {
        // Os detalhes são removidos automaticamente pela FK CASCADE
        String sql = "DELETE FROM calculos WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("✅ Cálculo removido: ID=" + id);
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar cálculo: " + e.getMessage(), e);
        }
    }

    // ── Busca detalhes de um cálculo ──────────────────────────────────────────
    private List<EmissaoFator> buscarDetalhes(int calculoId) throws SQLException {
        String sql = "SELECT * FROM calculo_detalhes WHERE calculo_id=?";
        List<EmissaoFator> fatores = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, calculoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final String  cat  = rs.getString("categoria");
                final String  desc = rs.getString("descricao");
                final double  val  = rs.getDouble("valor_kg");
                // Cria um fator "genérico" apenas para exibição (valor fixo)
                fatores.add(new EmissaoFator(cat, desc, "—") {
                    @Override public double calcular() { return val; }
                });
            }
        }
        return fatores;
    }

    // ── Mapeamento ResultSet → objeto ─────────────────────────────────────────
    private CalculoResult mapear(ResultSet rs) throws SQLException {
        int     usuarioId = rs.getInt("usuario_id");
        Usuario usuario   = usuarioDAO.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário ID=" + usuarioId + " não encontrado."));
        return new CalculoResult(
            rs.getInt("id"),
            usuario,
            rs.getDouble("total_emissao"),
            rs.getString("classificacao"),
            rs.getString("dica"),
            rs.getTimestamp("data_calculo").toLocalDateTime()
        );
    }
}
