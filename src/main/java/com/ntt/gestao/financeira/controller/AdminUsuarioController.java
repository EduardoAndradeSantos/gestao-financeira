package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.ImportacaoUsuarioResultadoDTO;
import com.ntt.gestao.financeira.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService service;

    public AdminUsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping(
            value = "/upload",
            consumes = "multipart/form-data"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ImportacaoUsuarioResultadoDTO uploadUsuarios(
            @RequestPart("file") MultipartFile file
    ) {
        return service.importarUsuariosViaExcel(file);
    }
}
