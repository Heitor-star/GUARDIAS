package com.hegaf.guardians.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hegaf.guardians.dto.UsuarioCadastroDTO;
import com.hegaf.guardians.model.Usuario;
import com.hegaf.guardians.repository.UsuarioRepository;
import com.hegaf.guardians.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private UsuarioService usuarioService;

    // ── Painel ─────────────────────────────────────────────
    @GetMapping("/painel")
    public String painel() {
        return "admin/painel";
    }

    // ── Formulario novo usuario ────────────────────────────
    @GetMapping("/usuarios")
    public String novoUsuario(Model model) {
        model.addAttribute("cadastroDTO", new UsuarioCadastroDTO());
        return "admin/usuarios";
    }

    // ── Salvar novo usuario (admin) ────────────────────────
    @PostMapping("/salvar-usuario")
    public String salvarUsuario(
            @Valid @ModelAttribute("cadastroDTO") UsuarioCadastroDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            return "admin/usuarios";
        }

        try {
            usuarioService.cadastrar(dto, null);
            ra.addFlashAttribute("mensagem", "Usuario cadastrado com sucesso!");
            return "redirect:/admin/listar-usuarios";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erroSenha", ex.getMessage());
            model.addAttribute("cadastroDTO", dto);
            return "admin/usuarios";
        }
    }

    // ── Listagem de usuarios ───────────────────────────────
    @GetMapping("/listar-usuarios")
    public String listarUsuarios(
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc")  String dir,
            @RequestParam(defaultValue = "")     String busca,
            Model model,
            HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        Sort sort = dir.equals("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Usuario> pageResult = busca.isBlank()
                ? usuarioRepository.findAll(pageable)
                : usuarioRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        busca, busca, pageable);

        // ── DEBUG TEMPORARIO — remover depois ─────────────
        System.out.println("=== LISTAGEM DE USUARIOS ===");
        pageResult.getContent().forEach(u ->
            System.out.println(">>> USUARIO: " + u.getNome() + " | TIPO: " + u.getTipo())
        );
        System.out.println("============================");

        model.addAttribute("usuarios",      pageResult.getContent());
        model.addAttribute("totalPages",    pageResult.getTotalPages());
        model.addAttribute("currentPage",   page);
        model.addAttribute("size",          size);
        model.addAttribute("sortBy",        sortBy);
        model.addAttribute("dir",           dir);
        model.addAttribute("busca",         busca);
        model.addAttribute("totalUsuarios", pageResult.getTotalElements());
        model.addAttribute("sizeOptions",   List.of(10, 20, 30));

        return "admin/listar-usuarios";
    }

    // ── Deletar usuario ────────────────────────────────────
    @GetMapping("/deletarUsuario/{id}")
    public String deletarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/admin/listar-usuarios";
    }

    // ── Alterar perfil ─────────────────────────────────────
    @PostMapping("/alterarPerfil")
    @Transactional
    public String alterarPerfil(
            @RequestParam Long id,
            @RequestParam String tipo,
            @RequestParam(defaultValue = "")     String busca,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc")  String dir,
            RedirectAttributes ra) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado: " + id));

        System.out.println("=== ALTERAR PERFIL ===");
        System.out.println(">>> ID: " + id + " | TIPO ANTIGO: " + usuario.getTipo() + " | TIPO NOVO: " + tipo);

        usuario.setTipo(tipo);
        usuarioRepository.saveAndFlush(usuario);

        System.out.println(">>> SALVO! TIPO ATUAL: " + usuario.getTipo());
        System.out.println("======================");

        ra.addFlashAttribute("mensagem", "Perfil alterado com sucesso!");

        return "redirect:/admin/listar-usuarios?page=" + page
             + "&size="   + size
             + "&sortBy=" + sortBy
             + "&dir="    + dir
             + "&busca="  + busca
             + "&t="      + System.currentTimeMillis();
    }
}