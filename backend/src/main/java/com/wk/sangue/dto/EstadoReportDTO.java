package com.wk.sangue.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para o relatório de candidatos por estado.
 */
@Schema(description = "Representação do agrupamento de candidatos por unidade federativa (Estado)")
public class EstadoReportDTO {

    @Schema(description = "Sigla do estado brasileiro (UF)", example = "SE")
    private String estado;

    @Schema(description = "Total de candidatos cadastrados na respectiva UF", example = "22")
    private long quantidade;

    public EstadoReportDTO() {
    }

    public EstadoReportDTO(String estado, long quantidade) {
        this.estado = estado;
        this.quantidade = quantidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(long quantidade) {
        this.quantidade = quantidade;
    }
}
