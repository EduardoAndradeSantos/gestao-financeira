package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.request.UsuarioRequestDTO;
import com.ntt.gestao.financeira.dto.request.UsuarioUpdateRequestDTO;
import com.ntt.gestao.financeira.dto.response.UsuarioResponseDTO;
import com.ntt.gestao.financeira.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    /* ==========================================================
       ===================== SELF-SERVICE =======================
       ========================================================== */

    @GetMapping("/me")
    public UsuarioResponseDTO me() {
        return service.buscarUsuarioLogado();
    }

    @PutMapping("/me")
    public UsuarioResponseDTO atualizarMe(
            @Valid @RequestBody UsuarioUpdateRequestDTO dto
    ) {
        return service.atualizarUsuarioLogado(dto);
    }

    @PutMapping("/me/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void trocarSenha(
            @RequestParam String senhaAtual,
            @RequestParam String novaSenha
    ) {
        service.trocarSenhaUsuarioLogado(senhaAtual, novaSenha);
    }
}
