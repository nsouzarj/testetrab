package com.wk.sangue.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para o relatório de IMC médio por faixa etária de 10 em 10 anos.
 */
@Schema(description = "Representação do IMC médio agrupado por faixas etárias de dez em dez anos")
public class ImcFaixaEtariaReportDTO {

    @Schema(description = "Nome descritivo da faixa etária", example = "21 a 30")
    private String faixaEtaria;

    @Schema(description = "Valor médio do Índice de Massa Corporal (IMC) calculado para o grupo", example = "24.75")
    private double imcMedio;

    public ImcFaixaEtariaReportDTO() {
    }

    public ImcFaixaEtariaReportDTO(String faixaEtaria, double imcMedio) {
        this.faixaEtaria = faixaEtaria;
        this.imcMedio = imcMedio;
    }

    public String getFaixaEtaria() {
        return faixaEtaria;
    }

    public void setFaixaEtaria(String faixaEtaria) {
        this.faixaEtaria = faixaEtaria;
    }

    public double getImcMedio() {
        return imcMedio;
    }

    public void setImcMedio(double imcMedio) {
        this.imcMedio = imcMedio;
    }
}
