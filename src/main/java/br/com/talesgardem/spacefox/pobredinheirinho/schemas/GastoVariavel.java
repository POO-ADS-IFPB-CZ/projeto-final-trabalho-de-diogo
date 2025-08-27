package br.com.talesgardem.spacefox.pobredinheirinho.schemas;

import java.util.Date;

public class GastoVariavel extends Schema {
    private String descricao;
    private Date data;
    private float valor;
    private int parcelas;

    public GastoVariavel() {}

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setParcelas(int parcelas) {
        this.parcelas = parcelas;
    }
}
