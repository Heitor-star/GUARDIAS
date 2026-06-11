package com.hegaf.guardians.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Proxy para o ViaCEP.
 * O JS chama GET /api/cep/{cep} e recebe JSON com logradouro, bairro,
 * localidade e uf — campos já esperados pelo meu-perfil.js e cadastro.js.
 *
 * Já liberado no SecurityConfig:
 *   .requestMatchers("/api/cep/**").permitAll()
 */
@RestController
@RequestMapping("/api/cep")
public class CepController {

    private static final String VIACEP = "https://viacep.com.br/ws/{cep}/json/";

    @GetMapping("/{cep}")
    public ResponseEntity<?> buscar(@PathVariable String cep) {

        // Remove tudo que não for dígito e valida tamanho
        String cepLimpo = cep.replaceAll("\\D", "");
        if (cepLimpo.length() != 8) {
            return ResponseEntity.badRequest().body("CEP deve ter 8 dígitos.");
        }

        try {
            RestTemplate rest = new RestTemplate();
            ResponseEntity<Object> resposta = rest.getForEntity(VIACEP, Object.class, cepLimpo);

            // ViaCEP retorna {"erro":true} para CEPs inexistentes
            if (resposta.getBody() != null &&
                resposta.getBody().toString().contains("erro=true")) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(resposta.getBody());

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}