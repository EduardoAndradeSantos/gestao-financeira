package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.service.RelatorioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @GetMapping("/{numeroConta}/excel")
    public ResponseEntity<byte[]> baixarExcel(@PathVariable String numeroConta) {

        byte[] arquivo = service.gerarRelatorioExcel(numeroConta);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-financeiro.xlsx"
                )
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .body(arquivo);
    }

    @GetMapping("/{numeroConta}/pdf")
    public ResponseEntity<byte[]> baixarPdf(@PathVariable String numeroConta) {

        byte[] arquivo = service.gerarRelatorioPdf(numeroConta);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio-financeiro.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(arquivo);
    }

}
