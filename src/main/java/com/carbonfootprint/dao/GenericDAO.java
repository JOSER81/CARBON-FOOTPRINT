package com.carbonfootprint.dao;

import java.util.List;
import java.util.Optional;

/**
 * INTERFACE GENÉRICA — define o contrato CRUD para qualquer entidade.
 * Demonstra ABSTRAÇÃO e uso de Generics.
 *
 * @param <T>  tipo da entidade (Usuario, CalculoResult…)
 * @param <ID> tipo do identificador (Integer)
 */
public interface GenericDAO<T, ID> {

    /** CREATE — persiste uma nova entidade. */
    T salvar(T entidade);

    /** READ — busca por ID. */
    Optional<T> buscarPorId(ID id);

    /** READ — retorna todas as entidades. */
    List<T> listarTodos();

    /** UPDATE — atualiza uma entidade existente. */
    boolean atualizar(T entidade);

    /** DELETE — remove pelo ID. */
    boolean deletar(ID id);
}
