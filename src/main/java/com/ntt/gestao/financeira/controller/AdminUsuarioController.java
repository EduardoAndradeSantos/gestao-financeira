package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.ImportacaoUsuarioResultadoDTO;
import com.ntt.gestao.financeira.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller administrativo para operações avançadas de usuários.
 *
 * Este controller expõe endpoints restritos a administradores,
 * como importação em massa de usuários via arquivo Excel.
 */
@RestController
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    // Service responsável pela lógica de negócio relacionada a usuários
    private final UsuarioService service;

    public AdminUsuarioController(UsuarioService service) {
        this.service = service;
    }

    /**
     * Realiza a importação de usuários a partir de um arquivo Excel.
     *
     * - Endpoint restrito a usuários com role ADMIN
     * - O arquivo deve ser enviado no formato multipart/form-data
     * - Retorna um resumo do resultado da importação
     *   (sucessos, falhas e mensagens)
     */
    @PostMapping(
            value = "/upload",
            consumes = "multipart/form-data"
    )
    @ResponseStatus(HttpStatus.CREATED) // Retorna HTTP 201 em caso de sucesso
    @PreAuthorize("hasRole('ADMIN')")   // Garante acesso apenas a administradores
    public ImportacaoUsuarioResultadoDTO uploadUsuarios(
            @RequestPart("file") MultipartFile file // Arquivo Excel enviado na requisição
    ) {
        return service.importarUsuariosViaExcel(file);
    }
}
