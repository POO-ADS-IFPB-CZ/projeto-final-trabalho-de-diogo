package br.com.talesgardem.spacefox.pobredinheirinho.schemas;

import java.time.LocalDateTime;
import java.util.Date;

public class GastoFixo extends Schema {
    private String descricao;
    private Date data;
    private float valor;

    public GastoFixo() {}

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
}