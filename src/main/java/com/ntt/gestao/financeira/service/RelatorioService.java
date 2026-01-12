package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.entity.Transacao;
import com.ntt.gestao.financeira.entity.TipoTransacao;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.RecursoNaoEncontradoException;
import com.ntt.gestao.financeira.repository.TransacaoRepository;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
public class RelatorioService {

    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;

    public RelatorioService(
            UsuarioRepository usuarioRepository,
            TransacaoRepository transacaoRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public byte[] gerarRelatorioExcel(String numeroConta) {

        Usuario usuario = usuarioRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        List<Transacao> transacoes =
                transacaoRepository.findByUsuarioId(usuario.getId())
                        .stream()
                        .sorted((a, b) -> a.getData().compareTo(b.getData()))
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

            // =======================
            // Estilos
            // =======================
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowIdx = 0;

            // =======================
            // Dados do usuário
            // =======================
            Row contaRow = sheet.createRow(rowIdx++);
            contaRow.createCell(0).setCellValue("Conta:");
            contaRow.getCell(0).setCellStyle(boldStyle);
            contaRow.createCell(1).setCellValue(usuario.getNumeroConta());

            Row nomeRow = sheet.createRow(rowIdx++);
            nomeRow.createCell(0).setCellValue("Nome:");
            nomeRow.getCell(0).setCellStyle(boldStyle);
            nomeRow.createCell(1).setCellValue(usuario.getNome());

            rowIdx++;

            // =======================
            // Totais
            // =======================
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

            // =======================
            // Cabeçalho da tabela
            // =======================
            Row tableHeader = sheet.createRow(rowIdx++);
            tableHeader.createCell(0).setCellValue("Data");
            tableHeader.createCell(1).setCellValue("Descrição");
            tableHeader.createCell(2).setCellValue("Tipo");
            tableHeader.createCell(3).setCellValue("Categoria");
            tableHeader.createCell(4).setCellValue("Valor");

            for (int i = 0; i <= 4; i++) {
                tableHeader.getCell(i).setCellStyle(headerStyle);
            }

            // =======================
            // Linhas da tabela
            // =======================
            for (Transacao t : transacoes) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(t.getData().toString());
                row.createCell(1).setCellValue(t.getDescricao());
                row.createCell(2).setCellValue(t.getTipo().name());

                String categoria = "-";
                if (t.getTipo() == TipoTransacao.RETIRADA) {
                    categoria = t.getCategoria().name();
                }
                row.createCell(3).setCellValue(categoria);

                row.createCell(4).setCellValue(moeda.format(t.getValor()));
            }

            // =======================
            // Ajuste de colunas
            // =======================
            for (int i = 0; i <= 4; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório Excel", e);
        }
    }
}
