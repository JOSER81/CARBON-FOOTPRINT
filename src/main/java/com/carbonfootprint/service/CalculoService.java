package com.carbonfootprint.service;

import com.carbonfootprint.dao.CalculoDAO;
import com.carbonfootprint.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Camada de serviço para CalculoResult.
 * Monta o objeto de domínio a partir dos dados fornecidos pela UI,
 * aciona o cálculo e persiste via DAO.
 */
public class CalculoService {

    private final CalculoDAO dao;  // COMPOSIÇÃO

    public CalculoService() {
        this.dao = new CalculoDAO();
    }

    /**
     * Monta, calcula e persiste um novo resultado para o usuário.
     * POLIMORFISMO em ação: cada EmissaoFator.calcular() se comporta
     * de forma diferente conforme a subclasse instanciada.
     */
    public CalculoResult calcularEPersistir(
            Usuario  usuario,
            double   kmSemana,
            String   transporte,
            String   energia,
            String   alimentacao,
            String   consumo,
            String   residuos) {

        CalculoResult resultado = new CalculoResult(usuario);

        // Instancia cada subclasse — POLIMORFISMO
        resultado.adicionarFator(new TransporteEmissao(
                kmSemana,
                TransporteEmissao.TipoTransporte.fromLabel(transporte)));

        resultado.adicionarFator(new EnergiaEmissao(
                EnergiaEmissao.NivelConsumo.fromLabel(energia)));

        resultado.adicionarFator(new AlimentacaoEmissao(
                AlimentacaoEmissao.PadraoAlimentar.fromLabel(alimentacao)));

        resultado.adicionarFator(new ConsumoEmissao(
                ConsumoEmissao.FrequenciaCompra.fromLabel(consumo)));

        resultado.adicionarFator(new ResiduosEmissao(
                ResiduosEmissao.NivelReciclagem.fromLabel(residuos)));

        return dao.salvar(resultado);
    }

    public Optional<CalculoResult> buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<CalculoResult> listarTodos() {
        return dao.listarTodos();
    }

    public List<CalculoResult> listarPorUsuario(int usuarioId) {
        return dao.listarPorUsuario(usuarioId);
    }

    public boolean atualizar(CalculoResult c) {
        c.recalcular();
        return dao.atualizar(c);
    }

    public boolean deletar(int id) {
        return dao.deletar(id);
    }
}
