package com.cryptospace.shared;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransacaoResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tipo;
    private BigDecimal valorBtc;
    private String descricao;
    private String dataMovimentacao;

    public TransacaoResponse() {
    }

    public TransacaoResponse(Long id, String tipo, BigDecimal valorBtc, String descricao, String dataMovimentacao) {
        this.id = id;
        this.tipo = tipo;
        this.valorBtc = valorBtc;
        this.descricao = descricao;
        this.dataMovimentacao = dataMovimentacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValorBtc() {
        return valorBtc;
    }

    public void setValorBtc(BigDecimal valorBtc) {
        this.valorBtc = valorBtc;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(String dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }
}
