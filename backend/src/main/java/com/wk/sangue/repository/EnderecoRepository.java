package com.wk.sangue.repository;

import com.wk.sangue.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para operações na tabela de Endereços.
 */
@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
