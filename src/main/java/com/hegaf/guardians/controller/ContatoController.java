package com.hegaf.guardians.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hegaf.guardians.dto.ContatoDTO;
import com.hegaf.guardians.model.Contato;
import com.hegaf.guardians.repository.ContatoRepository;
import com.hegaf.guardians.service.ContatoService;

import jakarta.validation.Valid;

@Controller
public class ContatoController {

    @Autowired private ContatoService contatoService;
    @Autowired private ContatoRepository contatoRepository;

    // ── Exibir formulario (publico) ────────────────────────
    @GetMapping("/contato")
    public String exibirContato(Model model, Authentication auth) {
        ContatoDTO dto = new ContatoDTO();

        // Pre-preenche nome e email se usuario estiver logado
        if (auth != null && auth.isAuthenticated()
                && !auth.getName().equals("anonymousUser")) {
            dto.setEmail(auth.getName());
        }

        model.addAttribute("contatoDTO", dto);
        return "contato";
    }

    // ── Processar envio ────────────────────────────────────
    @PostMapping("/contato/enviar")
    public String enviar(
            @Valid @ModelAttribute("contatoDTO") ContatoDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {

        if (result.hasErrors()) {
            return "contato";
        }

        try {
            contatoService.salvar(dto);
            ra.addFlashAttribute("sucesso", "Mensagem enviada com sucesso! Retornaremos em breve.");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao enviar mensagem. Tente novamente.");
        }

        return "redirect:/contato?enviado";
    }

    // ── Painel admin — listar mensagens ───────────────────
    @GetMapping("/operador/contatos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String listarContatos(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Contato> pagina = contatoRepository.findAllByOrderByDataEnvioDesc(
                PageRequest.of(page, size));

        model.addAttribute("contatos",     pagina.getContent());
        model.addAttribute("totalPages",   pagina.getTotalPages());
        model.addAttribute("currentPage",  page);
        model.addAttribute("naoLidas",     contatoService.contarNaoLidas());

        return "operador/contatos";
    }

    // ── Admin — ver mensagem e marcar como lida ────────────
    @GetMapping("/operador/contatos/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String verContato(@PathVariable Long id, Model model) {
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensagem nao encontrada"));
        contatoService.marcarLida(id);
        model.addAttribute("contato", contato);
        return "operador/contato-detalhe";
    }

    // ── Admin — responder ──────────────────────────────────
    @PostMapping("/operador/contatos/{id}/responder")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String responder(
            @PathVariable Long id,
            @RequestParam String resposta,
            RedirectAttributes ra) {

        contatoService.responder(id, resposta);
        ra.addFlashAttribute("sucesso", "Resposta registrada com sucesso!");
        return "redirect:/admin/contatos/" + id;
    }

    // ── Admin — deletar ────────────────────────────────────
    @PostMapping("/operador/contatos/{id}/deletar")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deletar(@PathVariable Long id, RedirectAttributes ra) {
        contatoService.deletar(id);
        ra.addFlashAttribute("sucesso", "Mensagem removida!");
        return "redirect:/operador/contatos";
    }
}