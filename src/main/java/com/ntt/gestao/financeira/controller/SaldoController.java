package com.ntt.gestao.financeira.controller;

import com.ntt.gestao.financeira.dto.response.SaldoResponseDTO;
import com.ntt.gestao.financeira.security.UserPrincipal;
import com.ntt.gestao.financeira.service.SaldoClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/saldo")
public class SaldoController {

    private final SaldoClient saldoClient;

    public SaldoController(SaldoClient saldoClient) {
        this.saldoClient = saldoClient;
    }

    @GetMapping
    public SaldoResponseDTO consultarSaldo(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return saldoClient.buscarSaldo(user.getNumeroConta());
    }
}
