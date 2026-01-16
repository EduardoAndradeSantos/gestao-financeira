package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.request.TransacaoRequestDTO;
import com.ntt.gestao.financeira.dto.request.TransacaoTransferenciaDTO;
import com.ntt.gestao.financeira.dto.response.TransacaoResponseDTO;
import com.ntt.gestao.financeira.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pelas operações de transações financeiras.
 *
 * Gerencia:
 * - Depósitos
 * - Retiradas
 * - Transferências
 * - Consulta e exclusão de transações
 *
 * Todas as operações são realizadas no contexto
 * do usuário autenticado.
 */
@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    // Service que contém as regras de negócio das transações
    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    /**
     * Cria uma nova transação financeira.
     *
     * O tipo da transação (DEPÓSITO, RETIRADA ou TRANSFERÊNCIA)
     * é definido no DTO de entrada.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Retorna HTTP 201
    public TransacaoResponseDTO criar(
            @Valid @RequestBody TransacaoRequestDTO dto // Dados validados da transação
    ) {
        return service.salvar(dto);
    }

    /**
     * Lista todas as transações do usuário autenticado.
     *
     * Não permite visualizar transações de outros usuários.
     */
    @GetMapping
    public List<TransacaoResponseDTO> listarDoUsuarioLogado() {
        return service.listarDoUsuarioLogado();
    }

    /**
     * Busca uma transação específica pelo ID.
     *
     * A validação de acesso garante que o usuário
     * só consiga acessar suas próprias transações.
     */
    @GetMapping("/{id}")
    public TransacaoResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    /**
     * Remove uma transação pelo ID.
     *
     * Retorna HTTP 204 (No Content) em caso de sucesso.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    /**
     * Realiza transferência entre contas.
     *
     * Esta operação gera duas transações:
     * - Débito na conta de origem
     * - Crédito na conta de destino
     */
    @PostMapping("/transferencia")
    @ResponseStatus(HttpStatus.OK)
    public void transferir(
            @Valid @RequestBody TransacaoTransferenciaDTO dto // Dados da transferência
    ) {
        service.transferir(dto);
    }
}
