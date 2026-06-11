package com.hegaf.guardians.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hegaf.guardians.model.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findByUsuarioId(Long usuarioId);
}
