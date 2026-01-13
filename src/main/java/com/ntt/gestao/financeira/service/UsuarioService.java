package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.request.UsuarioRequestDTO;
import com.ntt.gestao.financeira.dto.response.ImportacaoUsuarioResultadoDTO;
import com.ntt.gestao.financeira.dto.response.UsuarioResponseDTO;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.ConflitoDeDadosException;
import com.ntt.gestao.financeira.exception.RecursoNaoEncontradoException;
import com.ntt.gestao.financeira.repository.TransacaoRepository;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final TransacaoRepository transacaoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository,
                          TransacaoRepository transacaoRepository,
                          PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.transacaoRepository = transacaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {
        if (repository.existsByCpf(dto.cpf())) {
            throw new ConflitoDeDadosException("CPF já cadastrado!");
        }
        if (repository.existsByEmail(dto.email())) {
            throw new ConflitoDeDadosException("Email já cadastrado!");

        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .endereco(dto.endereco())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .numeroConta(gerarNumeroConta())
                .build();

        Usuario salvo = repository.save(usuario);
        return toDTO(salvo);
    }

    public List<UsuarioResponseDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado!"));
        return toDTO(usuario);
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado!"));

        usuario.setNome(dto.nome());
        usuario.setEndereco(dto.endereco());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));

        return toDTO(repository.save(usuario));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    private String gerarNumeroConta() {
        // Exemplo simples: 8 dígitos aleatórios
        return String.valueOf((int) (Math.random() * 90000000) + 10000000);
    }

    private UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEndereco(),
                usuario.getEmail(),
                usuario.getNumeroConta()
        );
    }

    public BigDecimal consultarSaldo(String numeroConta) {
        Usuario usuario = repository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

        return transacaoRepository.calcularSaldoUsuario(usuario.getId());
    }

    public ImportacaoUsuarioResultadoDTO importarUsuariosExcel(MultipartFile arquivo) {

        int sucesso = 0;
        int erros = 0;
        List<String> mensagensErro = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(arquivo.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalLinhas = sheet.getPhysicalNumberOfRows() - 1;

            for (int i = 1; i <= totalLinhas; i++) {
                Row row = sheet.getRow(i);

                try {
                    String nome = row.getCell(0).getStringCellValue();
                    String cpf = row.getCell(1).getStringCellValue();
                    String endereco = row.getCell(2).getStringCellValue();
                    String email = row.getCell(3).getStringCellValue();
                    String senha = row.getCell(4).getStringCellValue();

                    Usuario usuario = Usuario.builder()
                            .nome(nome)
                            .cpf(cpf)
                            .endereco(endereco)
                            .email(email)
                            .senha(passwordEncoder.encode(senha))
                            .numeroConta(gerarNumeroConta())
                            .build();

                    if (repository.existsByCpf(cpf) || repository.existsByEmail(email)) {
                        throw new RuntimeException("CPF ou email já cadastrado");
                    }

                    repository.save(usuario);
                    sucesso++;

                } catch (Exception e) {
                    erros++;
                    mensagensErro.add("Linha " + (i + 1) + ": " + e.getMessage());
                }
            }

            return new ImportacaoUsuarioResultadoDTO(
                    totalLinhas,
                    sucesso,
                    erros,
                    mensagensErro
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o arquivo Excel");
        }
    }

}
