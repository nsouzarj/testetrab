package com.wk.sangue.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para o relatório de média de idade por tipo sanguíneo.
 */
@Schema(description = "Representação da idade média calculada para cada tipo sanguíneo")
public class IdadeTipoSanguineoReportDTO {

    @Schema(description = "Tipo sanguíneo e fator Rh", example = "O-")
    private String tipoSanguineo;

    @Schema(description = "Idade média ponderada (em anos) dos doadores desse tipo", example = "53.06")
    private double idadeMedia;

    public IdadeTipoSanguineoReportDTO() {
    }

    public IdadeTipoSanguineoReportDTO(String tipoSanguineo, double idadeMedia) {
        this.tipoSanguineo = tipoSanguineo;
        this.idadeMedia = idadeMedia;
    }

    public String getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(String tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public double getIdadeMedia() {
        return idadeMedia;
    }

    public void setIdadeMedia(double idadeMedia) {
        this.idadeMedia = idadeMedia;
    }
}
