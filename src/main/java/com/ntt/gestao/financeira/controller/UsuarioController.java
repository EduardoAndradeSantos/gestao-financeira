package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.request.UsuarioUpdateRequestDTO;
import com.ntt.gestao.financeira.dto.response.UsuarioResponseDTO;
import com.ntt.gestao.financeira.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelas operações de autoatendimento do usuário.
 *
 * Permite que o próprio usuário:
 * - Consulte seus dados
 * - Atualize suas informações
 * - Troque sua senha
 *
 * Todas as operações são executadas no contexto
 * do usuário autenticado.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    // Service responsável pela lógica de negócio do usuário
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    /**
     * Retorna os dados do usuário autenticado.
     *
     * Endpoint equivalente ao "perfil do usuário".
     */
    @GetMapping("/me")
    public UsuarioResponseDTO me() {
        return service.buscarUsuarioLogado();
    }

    /**
     * Atualiza os dados do usuário autenticado.
     *
     * Não permite alterar dados sensíveis
     * como senha ou permissões.
     */
    @PutMapping("/me")
    public UsuarioResponseDTO atualizarMe(
            @Valid @RequestBody UsuarioUpdateRequestDTO dto // Dados atualizáveis do usuário
    ) {
        return service.atualizarUsuarioLogado(dto);
    }

    /**
     * Permite ao usuário autenticado trocar sua própria senha.
     *
     * A senha atual é validada antes da alteração,
     * garantindo segurança na operação.
     */
    @PutMapping("/me/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna HTTP 204 em caso de sucesso
    public void trocarSenha(
            @RequestParam String senhaAtual, // Senha atual do usuário
            @RequestParam String novaSenha    // Nova senha desejada
    ) {
        service.trocarSenhaUsuarioLogado(senhaAtual, novaSenha);
    }
}
