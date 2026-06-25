package com.wk.sangue.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para o relatório de quantidade de possíveis doadores para cada tipo sanguíneo receptor.
 */
@Schema(description = "Representação da contagem de doadores aptos compatíveis para cada tipo de sangue receptor")
public class DoadoresPorReceptorReportDTO {

    @Schema(description = "Tipo sanguíneo do receptor", example = "AB+")
    private String tipoSanguineoReceptor;

    @Schema(description = "Quantidade de doadores saudáveis cadastrados compatíveis com o receptor", example = "204")
    private long quantidadeDoadores;

    public DoadoresPorReceptorReportDTO() {
    }

    public DoadoresPorReceptorReportDTO(String tipoSanguineoReceptor, long quantidadeDoadores) {
        this.tipoSanguineoReceptor = tipoSanguineoReceptor;
        this.quantidadeDoadores = quantidadeDoadores;
    }

    public String getTipoSanguineoReceptor() {
        return tipoSanguineoReceptor;
    }

    public void setTipoSanguineoReceptor(String tipoSanguineoReceptor) {
        this.tipoSanguineoReceptor = tipoSanguineoReceptor;
    }

    public long getQuantidadeDoadores() {
        return quantidadeDoadores;
    }

    public void setQuantidadeDoadores(long quantidadeDoadores) {
        this.quantidadeDoadores = quantidadeDoadores;
    }
}
