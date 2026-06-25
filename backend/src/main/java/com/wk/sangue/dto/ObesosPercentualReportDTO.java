package com.wk.sangue.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para o relatório de percentual de obesos por sexo.
 */
@Schema(description = "Representação estatística da proporção de doadores obesos (IMC > 30) divididos por gênero")
public class ObesosPercentualReportDTO {

    @Schema(description = "Percentual de doadores do sexo Masculino considerados obesos", example = "22.15")
    private double percentualHomens;

    @Schema(description = "Percentual de doadores do sexo Feminino consideradas obesas", example = "18.54")
    private double percentualMulheres;

    public ObesosPercentualReportDTO() {
    }

    public ObesosPercentualReportDTO(double percentualHomens, double percentualMulheres) {
        this.percentualHomens = percentualHomens;
        this.percentualMulheres = percentualMulheres;
    }

    public double getPercentualHomens() {
        return percentualHomens;
    }

    public void setPercentualHomens(double percentualHomens) {
        this.percentualHomens = percentualHomens;
    }

    public double getPercentualMulheres() {
        return percentualMulheres;
    }

    public void setPercentualMulheres(double percentualMulheres) {
        this.percentualMulheres = percentualMulheres;
    }
}
