package com.hegaf.guardians.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hegaf.guardians.model.Evento;	

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
}