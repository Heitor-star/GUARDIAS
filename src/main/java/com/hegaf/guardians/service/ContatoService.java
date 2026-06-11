package com.hegaf.guardians.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hegaf.guardians.dto.ContatoDTO;
import com.hegaf.guardians.model.Contato;
import com.hegaf.guardians.repository.ContatoRepository;

@Service
public class ContatoService {

    @Autowired private ContatoRepository contatoRepository;

    @Transactional
    public void salvar(ContatoDTO dto) {
        Contato contato = new Contato();
        contato.setNome(dto.getNome());
        contato.setEmail(dto.getEmail());
        contato.setTelefone(dto.getTelefone());
        contato.setAssunto(dto.getAssunto());
        contato.setMensagem(dto.getMensagem());
        contato.setDataEnvio(LocalDateTime.now());
        contatoRepository.save(contato);
    }

    @Transactional
    public void marcarLida(Long id) {
        contatoRepository.findById(id).ifPresent(c -> {
            c.setLida(true);
            contatoRepository.save(c);
        });
    }

    @Transactional
    public void responder(Long id, String resposta) {
        contatoRepository.findById(id).ifPresent(c -> {
            c.setResposta(resposta);
            c.setRespondida(true);
            c.setLida(true);
            c.setDataResposta(LocalDateTime.now());
            contatoRepository.save(c);
        });
    }

    @Transactional
    public void deletar(Long id) {
        contatoRepository.deleteById(id);
    }

    public long contarNaoLidas() {
        return contatoRepository.countByLidaFalse();
    }
}