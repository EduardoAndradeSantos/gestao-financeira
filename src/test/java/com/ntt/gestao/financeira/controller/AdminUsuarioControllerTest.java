package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.ImportacaoUsuarioResultadoDTO;
import com.ntt.gestao.financeira.security.JwtService;
import com.ntt.gestao.financeira.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUsuarioController.class)
@AutoConfigureMockMvc
class AdminUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveImportarUsuariosViaExcel() throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "usuarios.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        new byte[]{1, 2, 3}
                );

        when(usuarioService.importarUsuariosViaExcel(any()))
                .thenReturn(new ImportacaoUsuarioResultadoDTO(
                        1,
                        1,
                        0,
                        List.of()
                ));

        mockMvc.perform(
                        multipart("/admin/usuarios/upload")
                                .file(file)
                                .with(csrf())
                )
                .andExpect(status().isCreated());
    }
}
