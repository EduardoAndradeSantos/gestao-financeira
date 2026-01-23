package com.ntt.gestao.financeira.service;

import com.ntt.gestao.financeira.dto.request.UsuarioUpdateRequestDTO;
import com.ntt.gestao.financeira.dto.response.ImportacaoUsuarioResultadoDTO;
import com.ntt.gestao.financeira.entity.Usuario;
import com.ntt.gestao.financeira.exception.ConflitoDeDadosException;
import com.ntt.gestao.financeira.exception.RecursoNaoEncontradoException;
import com.ntt.gestao.financeira.repository.UsuarioRepository;
import com.ntt.gestao.financeira.security.SecurityUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioRepository repository;
    private PasswordEncoder encoder;
    private UsuarioService service;
    private MockedStatic<SecurityUtils> securityMock;

    @BeforeEach
    void setup() {
        repository = mock(UsuarioRepository.class);
        encoder = mock(PasswordEncoder.class);
        service = new UsuarioService(repository, encoder);

        securityMock = mockStatic(SecurityUtils.class);
        securityMock.when(SecurityUtils::getUsuarioId).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        securityMock.close();
    }

    @Test
    void deveBuscarUsuarioLogadoComSucesso() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Edu")
                .cpf("123")
                .email("edu@email.com")
                .endereco("Rua A")
                .numeroConta("999")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        var response = service.buscarUsuarioLogado();

        assertEquals("Edu", response.nome());
        assertEquals("123", response.cpf());
    }

    @Test
    void deveFalharAoBuscarUsuarioLogadoQuandoNaoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.buscarUsuarioLogado());
    }

    @Test
    void deveAtualizarUsuarioLogado() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Antigo")
                .endereco("Antigo")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var dto = new UsuarioUpdateRequestDTO("Novo Nome", "Novo Endereço");

        var response = service.atualizarUsuarioLogado(dto);

        assertEquals("Novo Nome", response.nome());
        assertEquals("Novo Endereço", response.endereco());
        verify(repository).save(usuario);
    }

    @Test
    void deveFalharAoAtualizarUsuarioLogadoQuandoNaoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        var dto = new UsuarioUpdateRequestDTO("Nome", "Endereço");

        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.atualizarUsuarioLogado(dto));
    }

    @Test
    void deveTrocarSenhaComSucesso() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .senha("hash-antigo")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(encoder.matches("antiga", "hash-antigo")).thenReturn(true);
        when(encoder.encode("nova")).thenReturn("hash-nova");

        service.trocarSenhaUsuarioLogado("antiga", "nova");

        assertEquals("hash-nova", usuario.getSenha());
        verify(repository).save(usuario);
    }

    @Test
    void deveFalharQuandoSenhaAtualIncorreta() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .senha("hash")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(encoder.matches("errada", "hash")).thenReturn(false);

        assertThrows(ConflitoDeDadosException.class,
                () -> service.trocarSenhaUsuarioLogado("errada", "nova"));

        verify(repository, never()).save(any());
    }

    @Test
    void deveFalharAoTrocarSenhaQuandoUsuarioNaoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.trocarSenhaUsuarioLogado("a", "b"));
    }

    @Test
    void deveFalharAoImportarExcelVazio() {
        var file = new MockMultipartFile(
                "file", "usuarios.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new byte[0]
        );

        assertThrows(ConflitoDeDadosException.class,
                () -> service.importarUsuariosViaExcel(file));
    }

    @Test
    void deveImportarUsuariosComSucesso() throws Exception {
        var workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("nome");

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue("Edu");
        row.createCell(1).setCellValue("123");
        row.createCell(2).setCellValue("edu@email.com");
        row.createCell(3).setCellValue("Rua A");
        row.createCell(4).setCellValue("senha");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        var file = new MockMultipartFile(
                "file", "usuarios.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                out.toByteArray()
        );

        when(encoder.encode("senha")).thenReturn("hash");
        when(repository.existsByNumeroConta(any())).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ImportacaoUsuarioResultadoDTO result =
                service.importarUsuariosViaExcel(file);

        assertEquals(1, result.sucesso());
        assertEquals(0, result.erros());
        assertTrue(result.mensagensErro().isEmpty());

        verify(repository).save(any(Usuario.class));
    }

    @Test
    void deveIgnorarLinhaVaziaNoExcel() throws Exception {
        var workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        sheet.createRow(0);
        sheet.createRow(1); // linha vazia

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        var file = new MockMultipartFile(
                "file", "usuarios.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                out.toByteArray()
        );

        ImportacaoUsuarioResultadoDTO result =
                service.importarUsuariosViaExcel(file);

        assertEquals(0, result.sucesso());
        assertEquals(0, result.erros());
        assertTrue(result.mensagensErro().isEmpty());

        verify(repository, never()).save(any());
    }
}
