package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.service.RelatorioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pela geração de relatórios financeiros.
 *
 * Permite ao usuário autenticado exportar seus dados
 * em formatos PDF e Excel.
 */
@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    // Service responsável pela geração dos relatórios
    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    /**
     * Gera o relatório financeiro em formato PDF.
     *
     * O arquivo é retornado como download,
     * utilizando headers HTTP apropriados.
     */
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> gerarPdf() {

        // Gera o PDF com base nos dados do usuário autenticado
        byte[] pdf = service.gerarPdfUsuarioLogado();

        return ResponseEntity.ok()
                // Indica ao navegador que o conteúdo é um arquivo para download
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio.pdf")
                .contentType(MediaType.APPLICATION_PDF) // Define o tipo de conteúdo como PDF
                .body(pdf);
    }

    /**
     * Gera o relatório financeiro em formato Excel (.xlsx).
     *
     * O arquivo é retornado como download,
     * permitindo abertura em ferramentas como Excel ou LibreOffice.
     */
    @GetMapping("/excel")
    public ResponseEntity<byte[]> gerarExcel() {

        // Gera o arquivo Excel com base nos dados do usuário autenticado
        byte[] excel = service.gerarExcelUsuarioLogado();

        return ResponseEntity.ok()
                // Indica ao navegador que o conteúdo é um arquivo para download
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Tipo genérico para arquivos binários
                .body(excel);
    }
}
