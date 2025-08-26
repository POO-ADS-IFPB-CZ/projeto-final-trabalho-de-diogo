package br.com.talesgardem.spacefox.pobredinheirinho.schemas;

import java.time.LocalDateTime;
import java.util.Date;

public class GastoFixo {
    private String id;
    private String descricao;
    private Date data;
    private float valor;

    public String getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public Date getData() {
        return data;
    }

    public float getValor() {
        return valor;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String descricao) {
        this.descricao = descricao;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Gasto fixo [id=" + id + ", description=" + descricao + ", date=" + data + ", valor=" + valor + "]";
    }
}
