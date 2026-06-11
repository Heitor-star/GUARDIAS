package com.hegaf.guardians.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hegaf.guardians.model.Usuario;
import com.hegaf.guardians.repository.UsuarioRepository;

@Primary   // <- garante que o Spring usa ESTE e não um padrão gerado automaticamente
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("===========================================");
        System.out.println(">>> [LOGIN] Email recebido: '" + username + "'");

        Usuario usuario = repository.findByEmail(username);

        System.out.println(">>> [LOGIN] Usuário encontrado: " + (usuario != null));

        if (usuario == null) {
            System.out.println(">>> [LOGIN] ERRO: email não encontrado no banco!");
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }

        System.out.println(">>> [LOGIN] Senha BCrypt: " + usuario.getSenha());
        System.out.println(">>> [LOGIN] Tipo: " + usuario.getTipo());
        System.out.println(">>> [LOGIN] É BCrypt: " + usuario.getSenha().startsWith("$2a$"));
        System.out.println("===========================================");

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(List.of(new SimpleGrantedAuthority(usuario.getTipo())))
                .build();
    }
}
