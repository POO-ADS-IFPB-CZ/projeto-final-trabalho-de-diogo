package br.com.talesgardem.spacefox.pobredinheirinho.schemas;

import java.util.Date;

public class Lucro extends Schema {
    public String descricao;
    public boolean recorrente;
    public Date data;
    public float valor;
    public Lucro() {}

    public Date getData() {
        return data;
    }
    public void setData(Date data) {
        this.data = data;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public boolean isRecorrente() {
        return recorrente;
    }
    public void setRecorrente(boolean recorrente) {
        this.recorrente = recorrente;
    }
    public float getValor() {
        return valor;
    }
}
