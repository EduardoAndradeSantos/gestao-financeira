package com.ntt.gestao.financeira.repository;

import com.ntt.gestao.financeira.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.ntt.gestao.financeira.dto.response.DespesaPorCategoriaDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("""
    SELECT COALESCE(
        SUM(
            CASE 
                WHEN t.tipo = 'DEPOSITO' THEN t.valor
                WHEN t.tipo = 'RETIRADA' THEN -t.valor
                WHEN t.tipo = 'TRANSFERENCIA' AND t.usuario.id = :usuarioId THEN -t.valor 
                ELSE 0
            END
        ), 0
    )
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
""")
    BigDecimal calcularSaldoUsuario(Long usuarioId);

    @Query("""
    SELECT new com.ntt.gestao.financeira.dto.response.DespesaPorCategoriaDTO(
        t.categoria,
        SUM(t.valor)
    )
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
      AND t.tipo IN ('RETIRADA', 'TRANSFERENCIA')
    GROUP BY t.categoria
""")
    List<DespesaPorCategoriaDTO> totalDespesasPorCategoria(@Param("usuarioId") Long usuarioId);

    @Query("""
    SELECT COALESCE(SUM(t.valor), 0)
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
      AND t.tipo = 'DEPOSITO'
""")
    BigDecimal totalReceitas(Long usuarioId);

    @Query("""
    SELECT COALESCE(SUM(t.valor), 0)
    FROM Transacao t
    WHERE t.usuario.id = :usuarioId
      AND t.tipo IN ('RETIRADA', 'TRANSFERENCIA')
""")
    BigDecimal totalDespesas(Long usuarioId);

    List<Transacao> findByUsuarioId(Long usuarioId);

    List<Transacao> findByUsuarioNumeroContaOrderByDataHoraDesc(String numeroConta);
}
