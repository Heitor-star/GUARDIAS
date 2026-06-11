package com.hegaf.guardians.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hegaf.guardians.dto.UsuarioPerfilDTO;
import com.hegaf.guardians.dto.UsuarioSenhaDTO;
import com.hegaf.guardians.model.Endereco;
import com.hegaf.guardians.model.Usuario;
import com.hegaf.guardians.repository.EnderecoRepository;
import com.hegaf.guardians.service.UsuarioService;

@Controller
@RequestMapping("/meu-perfil")
public class PerfilController {

    @Autowired private EnderecoRepository enderecoRepository;
    @Autowired private UsuarioService usuarioService;

    // ── Exibir perfil ──────────────────────────────────────
    @GetMapping
    public String exibirPerfil(Model model, Authentication auth) {
        UsuarioPerfilDTO perfil = usuarioService.buscarPerfil(auth.getName());
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        model.addAttribute("perfil", perfil);
        model.addAttribute("enderecos", usuario.getEnderecos());
        model.addAttribute("senhaDTO", new UsuarioSenhaDTO());
        model.addAttribute("novoEndereco", new Endereco());
        return "meu-perfil";
    }

    // ── Atualizar dados pessoais ───────────────────────────
    @PostMapping("/dados")
    public String atualizarDados(
            @ModelAttribute("perfil") UsuarioPerfilDTO dto,
            Authentication auth,
            RedirectAttributes redirectAttrs) {
        try {
            usuarioService.atualizarPerfil(auth.getName(), dto);
            redirectAttrs.addFlashAttribute("sucesso", "Dados atualizados com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/meu-perfil#dados";
    }

    // ── Alterar senha ──────────────────────────────────────
    @PostMapping("/senha")
    public String alterarSenha(
            @ModelAttribute("senhaDTO") UsuarioSenhaDTO dto,
            Authentication auth,
            RedirectAttributes redirectAttrs) {
        try {
            usuarioService.alterarSenha(auth.getName(), dto);
            redirectAttrs.addFlashAttribute("sucesso", "Senha alterada com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/meu-perfil#senha";
    }

    // ── Atualizar foto ─────────────────────────────────────
    @PostMapping("/foto")
    public String atualizarFoto(
            @RequestParam("foto") MultipartFile foto,
            Authentication auth,
            RedirectAttributes redirectAttrs) {
        try {
            usuarioService.atualizarFoto(auth.getName(), foto);
            redirectAttrs.addFlashAttribute("sucesso", "Foto atualizada com sucesso!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/meu-perfil#foto";
    }

    // ── Adicionar endereço ─────────────────────────────────
    @PostMapping("/endereco/salvar")
    public String salvarEndereco(
            @ModelAttribute Endereco endereco,
            Authentication auth,
            RedirectAttributes redirectAttrs) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
            endereco.setUsuario(usuario);
            enderecoRepository.save(endereco);
            redirectAttrs.addFlashAttribute("sucesso", "Endereço adicionado com sucesso!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("erro", "Erro ao salvar endereço.");
        }
        return "redirect:/meu-perfil#enderecos";
    }

    // ── Remover endereço ───────────────────────────────────
    @PostMapping("/endereco/remover")
    public String removerEndereco(
            @RequestParam Long id,
            Authentication auth,
            RedirectAttributes redirectAttrs) {
        enderecoRepository.findById(id).ifPresent(e -> {
            if (e.getUsuario().getEmail().equals(auth.getName())) {
                enderecoRepository.delete(e);
            }
        });
        redirectAttrs.addFlashAttribute("sucesso", "Endereço removido com sucesso!");
        return "redirect:/meu-perfil#enderecos";
    }
}