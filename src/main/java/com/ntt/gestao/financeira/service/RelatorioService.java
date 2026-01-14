package com.ntt.gestao.financeira.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ntt.gestao.financeira.entity.Transacao;
import com.ntt.gestao.financeira.entity.TipoTransacao;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.RecursoNaoEncontradoException;
import com.ntt.gestao.financeira.repository.TransacaoRepository;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import com.ntt.gestao.financeira.security.SecurityUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class RelatorioService {

    private static final DateTimeFormatter DATA_HORA_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;

    public RelatorioService(
            UsuarioRepository usuarioRepository,
            TransacaoRepository transacaoRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
    }

    /* ==========================================================
       ===================== JWT CONTEXT ========================
       ========================================================== */

    private Usuario getUsuarioLogado() {
        String email = SecurityUtils.getEmailUsuarioLogado();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário logado não encontrado"));
    }

    /* ==========================================================
       ===================== EXCEL (JWT) ========================
       ========================================================== */

    public byte[] gerarExcelUsuarioLogado() {
        Usuario usuario = getUsuarioLogado();
        return gerarRelatorioExcel(usuario.getNumeroConta());
    }

    /* ==========================================================
       ====================== PDF (JWT) =========================
       ========================================================== */

    public byte[] gerarPdfUsuarioLogado() {
        Usuario usuario = getUsuarioLogado();
        return gerarRelatorioPdf(usuario.getNumeroConta());
    }

    /* ==========================================================
       ===================== RELATÓRIO EXCEL ====================
       ========================================================== */

    private byte[] gerarRelatorioExcel(String numeroConta) {

        Usuario usuario = usuarioRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .sorted((a, b) -> a.getDataHora().compareTo(b.getDataHora()))
                .toList();

        BigDecimal totalReceitas = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DEPOSITO)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RETIRADA
                        || t.getTipo() == TipoTransacao.TRANSFERENCIA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Relatório Financeiro");

            org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowIdx = 0;

            Row contaRow = sheet.createRow(rowIdx++);
            contaRow.createCell(0).setCellValue("Conta:");
            contaRow.getCell(0).setCellStyle(boldStyle);
            contaRow.createCell(1).setCellValue(usuario.getNumeroConta());

            Row nomeRow = sheet.createRow(rowIdx++);
            nomeRow.createCell(0).setCellValue("Nome:");
            nomeRow.getCell(0).setCellStyle(boldStyle);
            nomeRow.createCell(1).setCellValue(usuario.getNome());

            rowIdx++;

            Row receitasRow = sheet.createRow(rowIdx++);
            receitasRow.createCell(0).setCellValue("Total Receitas");
            receitasRow.getCell(0).setCellStyle(boldStyle);
            receitasRow.createCell(1).setCellValue(moeda.format(totalReceitas));

            Row despesasRow = sheet.createRow(rowIdx++);
            despesasRow.createCell(0).setCellValue("Total Despesas");
            despesasRow.getCell(0).setCellStyle(boldStyle);
            despesasRow.createCell(1).setCellValue(moeda.format(totalDespesas));

            Row saldoRow = sheet.createRow(rowIdx++);
            saldoRow.createCell(0).setCellValue("Saldo");
            saldoRow.getCell(0).setCellStyle(boldStyle);
            saldoRow.createCell(1).setCellValue(moeda.format(saldo));

            rowIdx++;

            Row tableHeader = sheet.createRow(rowIdx++);
            String[] headers = {
                    "Data/Hora", "Descrição", "Tipo",
                    "Categoria", "Conta Relacionada", "Valor"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = tableHeader.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (Transacao t : transacoes) {

                Row row = sheet.createRow(rowIdx++);

                String contaRel = t.getContaRelacionada() != null
                        ? t.getContaRelacionada().getNumeroConta()
                        : "-";

                row.createCell(0).setCellValue(
                        t.getDataHora().format(DATA_HORA_FORMATTER)
                );
                row.createCell(1).setCellValue(t.getDescricao());
                row.createCell(2).setCellValue(t.getTipo().name());

                String categoria = t.getTipo() == TipoTransacao.RETIRADA
                        ? t.getCategoria().name()
                        : "-";

                row.createCell(3).setCellValue(categoria);
                row.createCell(4).setCellValue(contaRel);
                row.createCell(5).setCellValue(moeda.format(t.getValor()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório Excel", e);
        }
    }

    /* ==========================================================
       ===================== RELATÓRIO PDF ======================
       ========================================================== */

    private byte[] gerarRelatorioPdf(String numeroConta) {

        Usuario usuario = usuarioRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .sorted((a, b) -> a.getDataHora().compareTo(b.getDataHora()))
                .toList();

        BigDecimal totalReceitas = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DEPOSITO)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RETIRADA
                        || t.getTipo() == TipoTransacao.TRANSFERENCIA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalReceitas.subtract(totalDespesas);

        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);

            document.open();

            Font tituloFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11);

            Paragraph titulo = new Paragraph("Relatório Financeiro", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            document.add(new Paragraph("Conta: " + usuario.getNumeroConta(), boldFont));
            document.add(new Paragraph("Nome: " + usuario.getNome(), normalFont));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Total Receitas: " + moeda.format(totalReceitas), normalFont));
            document.add(new Paragraph("Total Despesas: " + moeda.format(totalDespesas), normalFont));
            document.add(new Paragraph("Saldo: " + moeda.format(saldo), boldFont));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 4f, 2f, 3f, 3f, 2f});

            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            addHeader(table, "Data/Hora", headerFont);
            addHeader(table, "Descrição", headerFont);
            addHeader(table, "Tipo", headerFont);
            addHeader(table, "Categoria", headerFont);
            addHeader(table, "Conta Relacionada", headerFont);
            addHeader(table, "Valor", headerFont);

            Font cellFont = new Font(Font.HELVETICA, 10);

            for (Transacao t : transacoes) {

                String contaRel = t.getContaRelacionada() != null
                        ? t.getContaRelacionada().getNumeroConta()
                        : "-";

                table.addCell(new Phrase(
                        t.getDataHora().format(DATA_HORA_FORMATTER), cellFont
                ));
                table.addCell(new Phrase(t.getDescricao(), cellFont));
                table.addCell(new Phrase(t.getTipo().name(), cellFont));

                String categoria = t.getTipo() == TipoTransacao.RETIRADA
                        ? t.getCategoria().name()
                        : "-";

                table.addCell(new Phrase(categoria, cellFont));
                table.addCell(new Phrase(contaRel, cellFont));

                PdfPCell valorCell = new PdfPCell(
                        new Phrase(moeda.format(t.getValor()), cellFont)
                );
                valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(valorCell);
            }

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório PDF", e);
        }
    }

    private void addHeader(PdfPTable table, String text, Font font) {
        PdfPCell header = new PdfPCell(new Phrase(text, font));
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(6);
        table.addCell(header);
    }
}
