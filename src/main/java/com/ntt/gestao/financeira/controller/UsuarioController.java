package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.request.UsuarioRequestDTO;
import com.ntt.gestao.financeira.dto.response.ImportacaoUsuarioResultadoDTO;
import com.ntt.gestao.financeira.dto.response.UsuarioResponseDTO;
import com.ntt.gestao.financeira.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return service.salvar(dto);
    }

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO atualizar(@PathVariable Long id,
                                        @Valid @RequestBody UsuarioRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @GetMapping("/{conta}/saldo")
    public BigDecimal consultarSaldo(@PathVariable String conta) {
        return service.consultarSaldo(conta);
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ImportacaoUsuarioResultadoDTO uploadUsuarios(
            @RequestParam("file") MultipartFile file
    ) {
        return service.importarUsuariosExcel(file);
    }

}
