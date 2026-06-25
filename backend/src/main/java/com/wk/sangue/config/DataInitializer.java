package com.wk.sangue.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wk.sangue.dto.CandidatoInputDTO;
import com.wk.sangue.repository.CandidatoRepository;
import com.wk.sangue.service.CandidatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * Inicializador de dados que popula o banco MySQL automaticamente ao subir a API
 * caso a tabela de candidatos esteja vazia.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final CandidatoRepository candidatoRepository;
    private final CandidatoService candidatoService;
    private final ObjectMapper objectMapper;

    @Autowired
    public DataInitializer(CandidatoRepository candidatoRepository, 
                           CandidatoService candidatoService, 
                           ObjectMapper objectMapper) {
        this.candidatoRepository = candidatoRepository;
        this.candidatoService = candidatoService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        long totalCandidatos = candidatoRepository.count();
        
        if (totalCandidatos == 0) {
            System.out.println(">>> Banco de dados vazio. Iniciando carga automática do data.json...");
            try {
                ClassPathResource resource = new ClassPathResource("data.json");
                if (resource.exists()) {
                    try (InputStream inputStream = resource.getInputStream()) {
                        List<CandidatoInputDTO> candidatos = objectMapper.readValue(
                                inputStream, 
                                new TypeReference<List<CandidatoInputDTO>>() {}
                        );
                        int importados = candidatoService.importarCandidatos(candidatos);
                        System.out.println(">>> Carga automática concluída! " + importados + " candidatos importados com sucesso.");
                    }
                } else {
                    System.err.println(">>> Arquivo data.json não encontrado na pasta resources.");
                }
            } catch (Exception e) {
                System.err.println(">>> Erro ao realizar a carga automática de dados: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> Banco de dados já contém " + totalCandidatos + " registros. Ignorando carga automática.");
        }
    }
}
