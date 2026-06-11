package com.hegaf.guardians.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hegaf.guardians.dto.UsuarioCadastroDTO;
import com.hegaf.guardians.service.UsuarioService;

import jakarta.validation.Valid;

/**
 * Cadastro público  → GET /cadastro  + POST /salvar → redireciona para /login
 * Cadastro admin    → POST /salvar   (vindo de admin/usuarios.html com cadastroDTO)
 *
 * cadastro.html       usa th:object="${usuario}"
 * admin/usuarios.html usa th:object="${cadastroDTO}"
 */
@Controller
public class CadastroController {

    @Autowired
    private UsuarioService usuarioService;

    // ── GET /cadastro ─────────────────────────────────────

    @GetMapping("/cadastro")
    public String exibirCadastro(Model model) {
        model.addAttribute("usuario", new UsuarioCadastroDTO());
        return "cadastro";
    }

    // ── POST /salvar ──────────────────────────────────────

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute UsuarioCadastroDTO usuarioCadastroDTO,
            BindingResult result,
            Authentication auth,
            RedirectAttributes ra,
            Model model) {

        boolean isAdmin = auth != null &&
            auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // Erros de validação — volta ao formulário correto com objeto preservado
        if (result.hasErrors()) {
            if (isAdmin) {
                model.addAttribute("cadastroDTO", usuarioCadastroDTO);
                return "admin/usuarios";
            }
            model.addAttribute("usuario", usuarioCadastroDTO);
            return "cadastro";
        }

        // Não-admin sempre cadastra como ROLE_USER
        if (!isAdmin) {
            usuarioCadastroDTO.setTipo("ROLE_USER");
        }

        try {
            usuarioService.cadastrar(usuarioCadastroDTO, null);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erroSenha", ex.getMessage());
            if (isAdmin) {
                model.addAttribute("cadastroDTO", usuarioCadastroDTO);
                return "admin/usuarios";
            }
            model.addAttribute("usuario", usuarioCadastroDTO);
            return "cadastro";
        }

        if (isAdmin) {
            ra.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso!");
            return "redirect:/admin/listar-usuarios";
        }

        ra.addFlashAttribute("mensagem", "Cadastro realizado! Faça seu login.");
        return "redirect:/login";
    }
}