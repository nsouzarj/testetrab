package com.wk.sangue.repository;

import com.wk.sangue.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para operações na tabela de Candidatos.
 */
@Repository
public interface CandidatoRepository extends JpaRepository<Candidato, Long> {
    
    /**
     * Verifica se um candidato já existe no banco através de seu CPF.
     * 
     * @param cpf CPF a ser verificado
     * @return true se existir, false caso contrário
     */
    boolean existsByCpf(String cpf);
}
