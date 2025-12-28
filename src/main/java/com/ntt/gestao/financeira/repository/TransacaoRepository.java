package com.ntt.gestao.financeira.repository;

import com.ntt.gestao.financeira.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

}
