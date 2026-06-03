package com.cryptospace.shared;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarteiraResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String hash;
    private BigDecimal saldoTotalBtc;
    private List<TransacaoResponse> transacoes;

    public CarteiraResponse() {
        this.saldoTotalBtc = BigDecimal.ZERO;
        this.transacoes = new ArrayList<>();
    }

    public CarteiraResponse(String hash, BigDecimal saldoTotalBtc, List<TransacaoResponse> transacoes) {
        this.hash = hash;
        this.saldoTotalBtc = saldoTotalBtc;
        this.transacoes = transacoes;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public BigDecimal getSaldoTotalBtc() {
        return saldoTotalBtc;
    }

    public void setSaldoTotalBtc(BigDecimal saldoTotalBtc) {
        this.saldoTotalBtc = saldoTotalBtc;
    }

    public List<TransacaoResponse> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<TransacaoResponse> transacoes) {
        this.transacoes = transacoes;
    }
}
