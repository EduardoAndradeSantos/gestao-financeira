package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.request.TransacaoRequestDTO;
import com.ntt.gestao.financeira.dto.request.TransacaoTransferenciaDTO;
import com.ntt.gestao.financeira.dto.response.TransacaoResponseDTO;
import com.ntt.gestao.financeira.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransacaoResponseDTO criar(@Valid @RequestBody TransacaoRequestDTO dto) {
        return service.salvar(dto);
    }

    @GetMapping
    public List<TransacaoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public TransacaoResponseDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public TransacaoResponseDTO atualizar(@PathVariable Long id,
                                          @Valid @RequestBody TransacaoRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @PostMapping("/transferencia")
    @ResponseStatus(HttpStatus.OK)
    public void transferir(@Valid @RequestBody TransacaoTransferenciaDTO dto) {
        service.transferir(dto);
    }
}
