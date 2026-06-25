package com.wk.sangue.controller;

import com.wk.sangue.dto.*;
import com.wk.sangue.service.CandidatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gerenciar candidatos a doação de sangue e relatórios.
 * Disponibiliza as APIs no caminho /api/candidatos.
 */
@RestController
@RequestMapping("/api/candidatos")
@CrossOrigin(origins = "*") // Permite chamadas de qualquer origem para testes na rede local
@Tag(name = "Banco de Sangue - Candidatos", description = "APIs para importação de doadores e geração de relatórios estatísticos e de compatibilidade sanguínea")
public class CandidatoController {

    private final CandidatoService candidatoService;

    @Autowired
    public CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = candidatoService;
    }

    /**
     * Endpoint para importar uma lista de candidatos.
     * Recebe um array de JSON com os dados originais.
     */
    @Operation(
        summary = "Importar candidatos a doação",
        description = "Recebe uma lista de candidatos em formato JSON, realiza o processamento dos dados salvando a localidade e dados pessoais de forma normalizada (3NF) no MySQL. Ignora registros com CPFs já cadastrados para evitar duplicidades."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Candidatos processados e importados com sucesso. Retorna o número de novos registros salvos.",
                     content = @Content(schema = @Schema(implementation = Integer.class))),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou formato de JSON corrompido."),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor ao processar os dados ou persistir no banco.")
    })
    @PostMapping("/importar")
    public ResponseEntity<Integer> importar(@RequestBody List<CandidatoInputDTO> dtos) {
        int novosImportados = candidatoService.importarCandidatos(dtos);
        return ResponseEntity.ok(novosImportados);
    }

    /**
     * Relatório 1: Quantidade de candidatos em cada estado.
     */
    @Operation(
        summary = "Relatório de candidatos por estado",
        description = "Gera uma lista agrupando os doadores cadastrados pelo estado (UF) de sua residência, ordenado alfabeticamente."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso.",
                     content = @Content(schema = @Schema(implementation = EstadoReportDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro ao processar os dados do banco.")
    })
    @GetMapping("/relatorios/estado")
    public ResponseEntity<List<EstadoReportDTO>> getQuantidadePorEstado() {
        List<EstadoReportDTO> report = candidatoService.getCandidatosPorEstado();
        return ResponseEntity.ok(report);
    }

    /**
     * Relatório 2: IMC médio por faixa etária de 10 em 10 anos.
     */
    @Operation(
        summary = "Relatório de IMC médio por faixa etária",
        description = "Calcula a média do Índice de Massa Corporal (IMC) dos candidatos agrupados em faixas de idade de 10 em 10 anos (0 a 10, 11 a 20, 21 a 30, etc.) com base na data atual."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relatório de IMC por idade gerado com sucesso.",
                     content = @Content(schema = @Schema(implementation = ImcFaixaEtariaReportDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro ao processar as idades e pesos.")
    })
    @GetMapping("/relatorios/imc-faixa-etaria")
    public ResponseEntity<List<ImcFaixaEtariaReportDTO>> getImcMedioPorFaixaEtaria() {
        List<ImcFaixaEtariaReportDTO> report = candidatoService.getImcMedioPorFaixaEtaria();
        return ResponseEntity.ok(report);
    }

    /**
     * Relatório 3: Percentual de obesos entre homens e mulheres.
     */
    @Operation(
        summary = "Percentual de obesos por sexo",
        description = "Calcula a porcentagem de doadores obesos (IMC maior que 30) de forma separada entre homens e mulheres."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Percentual calculado com sucesso.",
                     content = @Content(schema = @Schema(implementation = ObesosPercentualReportDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro ao processar os cálculos estatísticos.")
    })
    @GetMapping("/relatorios/percentual-obesos")
    public ResponseEntity<ObesosPercentualReportDTO> getPercentualObesosPorSexo() {
        ObesosPercentualReportDTO report = candidatoService.getPercentualObesosPorSexo();
        return ResponseEntity.ok(report);
    }

    /**
     * Relatório 4: Média de idade para cada tipo sanguíneo.
     */
    @Operation(
        summary = "Média de idade por tipo sanguíneo",
        description = "Processa e calcula a média de idade (em anos) dos candidatos agrupados por cada um dos 8 tipos sanguíneos clássicos (A+, A-, B+, B-, AB+, AB-, O+, O-)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Médias de idade geradas com sucesso.",
                     content = @Content(schema = @Schema(implementation = IdadeTipoSanguineoReportDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro ao calcular médias do banco.")
    })
    @GetMapping("/relatorios/idade-tipo-sanguineo")
    public ResponseEntity<List<IdadeTipoSanguineoReportDTO>> getIdadeMediaPorTipoSanguineo() {
        List<IdadeTipoSanguineoReportDTO> report = candidatoService.getIdadeMediaPorTipoSanguineo();
        return ResponseEntity.ok(report);
    }

    /**
     * Relatório 5: Quantidade de possíveis doadores para cada tipo sanguíneo receptor.
     */
    @Operation(
        summary = "Quantidade de doadores aptos por tipo de receptor",
        description = "Filtra os candidatos aptos a doar sangue (idade entre 16 e 69 anos e peso acima de 50 Kg) e calcula o total de doadores compatíveis para cada tipo de sangue receptor."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compatibilidade e contagem de doadores gerada com sucesso.",
                     content = @Content(schema = @Schema(implementation = DoadoresPorReceptorReportDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro ao mapear a matriz de compatibilidade sanguínea.")
    })
    @GetMapping("/relatorios/doadores-por-receptor")
    public ResponseEntity<List<DoadoresPorReceptorReportDTO>> getQuantidadeDoadoresPorReceptor() {
        List<DoadoresPorReceptorReportDTO> report = candidatoService.getQuantidadeDoadoresPorReceptor();
        return ResponseEntity.ok(report);
    }
}
