package com.ntt.gestao.financeira.repository;

import com.ntt.gestao.financeira.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

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

}
