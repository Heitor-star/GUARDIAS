package com.hegaf.guardians.controller;
	

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hegaf.guardians.model.Acesso;
import com.hegaf.guardians.model.Evento;
import com.hegaf.guardians.model.Usuario;
import com.hegaf.guardians.repository.AcessoRepository;
import com.hegaf.guardians.repository.EventoRepository;
import com.hegaf.guardians.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AppController {

    @Autowired private AcessoRepository acessoRepository;
    @Autowired private EventoRepository eventoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    // ── Home ───────────────────────────────────────────────
    @GetMapping({"/", "/home"})
    public String home(HttpServletRequest request, Model model) {
        String ip = request.getRemoteAddr();
        LocalDate hoje = LocalDate.now();
        if (acessoRepository.findByIdentificadorAndDataAcesso(ip, hoje).isEmpty()) {
            acessoRepository.save(new Acesso(ip, hoje));
        }
        model.addAttribute("visitantesHoje", acessoRepository.countByDataAcesso(hoje));
        return "home";
    }

    // ── Calendario ─────────────────────────────────────────
    @GetMapping("/calendario")
    public String calendario() { return "calendario"; }

    // ── API Eventos ────────────────────────────────────────
    @GetMapping("/api/eventos")
    @ResponseBody
    public Map<String, Object> listarEventos(Authentication auth) {
        List<Long> inscritos = new ArrayList<>();
        if (auth != null && auth.isAuthenticated()
                && !auth.getName().equals("anonymousUser")) {
            Usuario usuario = usuarioRepository.findByEmail(auth.getName());
            if (usuario != null && usuario.getEventosInscritos() != null) {
                usuario.getEventosInscritos().forEach(e -> inscritos.add(e.getId()));
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("eventos",  eventoRepository.findAll());
        map.put("inscritos", inscritos);
        map.put("logado", auth != null && auth.isAuthenticated()
                && !auth.getName().equals("anonymousUser"));
        return map;
    }

    @GetMapping("/api/eventos/inscrever/{id}")
    public String inscrever(@PathVariable Long id, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()
                || auth.getName().equals("anonymousUser"))
            return "redirect:/login";

        Usuario usuario = usuarioRepository.findByEmail(auth.getName());
        Evento evento = eventoRepository.findById(id).orElse(null);
        if (usuario != null && evento != null) {
            usuario.getEventosInscritos().add(evento);
            usuarioRepository.save(usuario);
        }
        return "redirect:/calendario?inscrito";
    }

    @PostMapping("/api/eventos/cancelar")
    public String cancelar(@RequestParam Long eventoId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()
                || auth.getName().equals("anonymousUser"))
            return "redirect:/login";

        Usuario usuario = usuarioRepository.findByEmail(auth.getName());
        Evento evento = eventoRepository.findById(eventoId).orElse(null);
        if (usuario != null && evento != null) {
            usuario.getEventosInscritos().remove(evento);
            usuarioRepository.save(usuario);
        }
        return "redirect:/calendario";
    }

    // ── Relatorio de visitantes ────────────────────────────
    @GetMapping("/admin/visitantes")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OPERADOR')")
    public String visitantes(Model model) {
        LocalDate hoje = LocalDate.now();

        LocalDate inicioSemana = hoje.with(DayOfWeek.MONDAY);
        LocalDate fimSemana    = hoje.with(DayOfWeek.SUNDAY);
        LocalDate inicioMes    = hoje.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate fimMes       = hoje.with(TemporalAdjusters.lastDayOfMonth());

        List<Object[]> porDia = acessoRepository.countPorDia(hoje.minusDays(29), hoje);
        List<String> labels  = new ArrayList<>();
        List<Long>   valores = new ArrayList<>();
        for (Object[] row : porDia) {
            labels.add(row[0].toString());
            valores.add((Long) row[1]);
        }

        model.addAttribute("visitantesHoje",      acessoRepository.countByDataAcesso(hoje));
        model.addAttribute("visitantesSemana",    acessoRepository.countEntre(inicioSemana, fimSemana));
        model.addAttribute("visitantesMes",       acessoRepository.countEntre(inicioMes, fimMes));
        model.addAttribute("visitantesSeisMeses", acessoRepository.countEntre(hoje.minusMonths(6), hoje));
        model.addAttribute("visitantesAno",       acessoRepository.countEntre(hoje.minusYears(1), hoje));
        model.addAttribute("labels",  labels);
        model.addAttribute("valores", valores);

        return "admin/visitantes";
    }

    @GetMapping("/admin/visitantes/dados")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OPERADOR')")
    @ResponseBody
    public Map<String, Object> visitantesDados(
            @RequestParam String inicio,
            @RequestParam String fim) {

        LocalDate dataInicio = LocalDate.parse(inicio);
        LocalDate dataFim    = LocalDate.parse(fim);
        LocalDate hoje       = LocalDate.now();

        if (dataFim.isAfter(hoje))       dataFim    = hoje;
        if (dataInicio.isAfter(hoje))    dataInicio = hoje;
        if (dataInicio.isAfter(dataFim)) dataInicio = dataFim;

        List<Object[]> porDia = acessoRepository.countPorDia(dataInicio, dataFim);
        List<String> labels  = new ArrayList<>();
        List<Long>   valores = new ArrayList<>();
        for (Object[] row : porDia) {
            labels.add(row[0].toString());
            valores.add((Long) row[1]);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("labels",  labels);
        response.put("valores", valores);
        return response;
    }
}