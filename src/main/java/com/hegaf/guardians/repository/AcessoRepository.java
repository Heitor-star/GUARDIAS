package com.hegaf.guardians.repository;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hegaf.guardians.model.Acesso;

public interface AcessoRepository extends JpaRepository<Acesso, Long> {

    Optional<Acesso> findByIdentificadorAndDataAcesso(String identificador, LocalDate dataAcesso);

    long countByDataAcesso(LocalDate dataAcesso);

    // Total por dia — intervalo fixo (início a fim)
    @Query("SELECT a.dataAcesso, COUNT(a) FROM Acesso a " +
           "WHERE a.dataAcesso >= :inicio AND a.dataAcesso <= :fim " +
           "GROUP BY a.dataAcesso ORDER BY a.dataAcesso ASC")
    List<Object[]> countPorDia(@Param("inicio") LocalDate inicio,
                                @Param("fim")   LocalDate fim);

    // Total entre duas datas (cards de resumo)
    @Query("SELECT COUNT(a) FROM Acesso a WHERE a.dataAcesso >= :inicio AND a.dataAcesso <= :fim")
    long countEntre(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}