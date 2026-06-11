package com.hegaf.guardians.controller;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hegaf.guardians.model.Evento;
import com.hegaf.guardians.model.Usuario;
import com.hegaf.guardians.repository.EventoRepository;
import com.hegaf.guardians.repository.UsuarioRepository;

@Controller
@RequestMapping("/operador")
@PreAuthorize("hasAnyAuthority('ROLE_OPERADOR', 'ROLE_ADMIN')")
public class OperadorController {

    @Autowired private EventoRepository eventoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping("/painel")
    public String painel(Model model) {
        model.addAttribute("eventos", eventoRepository.findAll());
        return "operador/painel";
    }

    @PostMapping("/salvarEvento")
    public String salvarEvento(@ModelAttribute Evento evento) {
        eventoRepository.save(evento);
        return "redirect:/operador/painel";
    }

    @GetMapping("/deletarEvento/{id}")
    public String deletarEvento(@PathVariable Long id) {
        eventoRepository.deleteById(id);
        return "redirect:/operador/painel";
    }

    // ── Inscritos ──────────────────────────────────────────
    @GetMapping("/inscritos")
    public String inscritos(
            @RequestParam(required = false)      Long eventoId,
            @RequestParam(defaultValue = "10")   int limite,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc")  String dir,
            Model model) {

        model.addAttribute("eventos",          eventoRepository.findAll());
        model.addAttribute("eventoSelecionado", eventoId);
        model.addAttribute("limite",           limite);
        model.addAttribute("sortBy",           sortBy);
        model.addAttribute("dir",              dir);
        model.addAttribute("sizeOptions",      List.of(10, 20, 30, 50, 100));

        if (eventoId != null) {
            Comparator<Usuario> comparator = switch (sortBy) {
                case "email" -> Comparator.comparing(Usuario::getEmail, String.CASE_INSENSITIVE_ORDER);
                default      -> Comparator.comparing(Usuario::getNome,  String.CASE_INSENSITIVE_ORDER);
            };
            if (dir.equals("desc")) comparator = comparator.reversed();

            List<Usuario> inscritos = usuarioRepository.findAll().stream()
                .filter(u -> u.getEventosInscritos().stream()
                        .anyMatch(e -> e.getId().equals(eventoId)))
                .sorted(comparator)
                .limit(limite)
                .collect(Collectors.toList());

            model.addAttribute("inscritos", inscritos);
        } else {
            model.addAttribute("inscritos", List.of());
        }

        return "operador/inscritos";
    }

    @GetMapping("/inscritos/remover")
    public String removerInscrito(
            @RequestParam Long usuarioId,
            @RequestParam Long eventoId,
            @RequestParam(defaultValue = "10")   int limite,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc")  String dir) {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        Evento  evento  = eventoRepository.findById(eventoId).orElse(null);
        if (usuario != null && evento != null) {
            usuario.getEventosInscritos().remove(evento);
            usuarioRepository.save(usuario);
        }
        return "redirect:/operador/inscritos?eventoId=" + eventoId
             + "&limite=" + limite
             + "&sortBy=" + sortBy
             + "&dir="    + dir;
    }
}
