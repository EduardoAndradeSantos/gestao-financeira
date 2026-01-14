package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.request.UsuarioUpdateRequestDTO;
import com.ntt.gestao.financeira.dto.response.ImportacaoUsuarioResultadoDTO;
import com.ntt.gestao.financeira.dto.response.UsuarioResponseDTO;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.ConflitoDeDadosException;
import com.ntt.gestao.financeira.exception.RecursoNaoEncontradoException;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import com.ntt.gestao.financeira.security.SecurityUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ==========================================================
       ===================== SELF-SERVICE =======================
       ========================================================== */

    public UsuarioResponseDTO buscarUsuarioLogado() {

        Long usuarioId = SecurityUtils.getUsuarioId();

        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado"));

        return toResponseDTO(usuario);
    }

    public UsuarioResponseDTO atualizarUsuarioLogado(UsuarioUpdateRequestDTO dto) {

        Long usuarioId = SecurityUtils.getUsuarioId();

        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado"));

        usuario.setNome(dto.nome());
        usuario.setEndereco(dto.endereco());

        return toResponseDTO(repository.save(usuario));
    }

    public void trocarSenhaUsuarioLogado(String senhaAtual, String novaSenha) {

        Long usuarioId = SecurityUtils.getUsuarioId();

        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new ConflitoDeDadosException("Senha atual inválida");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        repository.save(usuario);
    }

    /* ==========================================================
       ===================== MAPEAMENTO =========================
       ========================================================== */

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getEndereco(),
                usuario.getNumeroConta()
        );
    }

    @Transactional
    public ImportacaoUsuarioResultadoDTO importarUsuariosViaExcel(MultipartFile file) {

        if (file.isEmpty()) {
            throw new ConflitoDeDadosException("Arquivo Excel vazio");
        }

        List<Usuario> usuariosSalvos = new ArrayList<>();
        List<String> erros = new ArrayList<>();
        int totalLinhas = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            totalLinhas = sheet.getLastRowNum();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || getCellAsString(row, 0).isBlank()) {
                    continue;
                }

                try {
                    Usuario usuario = Usuario.builder()
                            .nome(getCellAsString(row, 0))
                            .cpf(getCellAsString(row, 1))
                            .email(getCellAsString(row, 2))
                            .endereco(getCellAsString(row, 3))
                            .senha(passwordEncoder.encode(
                                    getCellAsString(row, 4)
                            ))
                            .numeroConta(gerarNumeroConta())
                            .build();

                    usuariosSalvos.add(repository.save(usuario));

                } catch (Exception e) {
                    erros.add("Linha " + (i + 1) + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar arquivo Excel", e);
        }

        int sucesso = usuariosSalvos.size();
        int totalErros = erros.size();

        return new ImportacaoUsuarioResultadoDTO(
                totalLinhas,
                sucesso,
                totalErros,
                erros
        );
    }

    private String gerarNumeroConta() {
        String numeroConta;
        do {
            numeroConta = String.valueOf(
                    10000000 + (int) (Math.random() * 90000000)
            );
        } while (repository.existsByNumeroConta(numeroConta));

        return numeroConta;
    }

    private String getCellAsString(Row row, int index) {
        if (row == null) {
            return "";
        }

        var cell = row.getCell(index);

        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                // evita .0 e problemas de CPF
                yield BigDecimal.valueOf(cell.getNumericCellValue())
                        .toPlainString();
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }


}
