package com.hegaf.guardians.config;

import com.hegaf.guardians.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    	provider.setUserDetailsService(customUserDetailsService);
    	provider.setPasswordEncoder(passwordEncoder());
    	return provider;
    }
    	
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    	return config.getAuthenticationManager();
    }
    
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
    		.authenticationProvider(authenticationProvider())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
            		.cacheControl(cache -> cache.disable())
            )
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/css/**", "/js/**", "/img/**", "/static/**", "/uploads/**").permitAll()
            		
            		
            		.requestMatchers("/", "/home", "/login", "/cadastro", "/salvar", "/calendario", "/api/eventos/**", "/api/cep/**", "/contato", "/contato/enviar").permitAll()
            		.requestMatchers("/meu-perfil", "/meu-perfil**").authenticated()
            		
            		.requestMatchers("/admin/visitantes").hasAnyAuthority("ROLE_ADMIN", "ROLE_OPERADOR")
            		.requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")
            		.requestMatchers("/operador/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_OPERADOR")
            		.anyRequest().authenticated()
            		)
            
            .formLogin(form -> form
            		.loginPage("/login")
            		.usernameParameter("username")
            		.passwordParameter("password")
            		.defaultSuccessUrl("/home", true)
            		.failureUrl("/login?error=true")
            		.permitAll()
            )
            .logout(logout -> logout
            		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            		.logoutSuccessUrl("/home")
            		.permitAll()
            );
    	
    	// -- Google --
    	// Descomentar apos preencher as credenciais no application.properties
    	// http.oauth2Login(oauth2 -> oauth2
    	//		.loginPage("/login")
    	//		.defaultSuccessUrl("/home", true)
    	//);
            
            		

             
            

        return http.build();
    }
}