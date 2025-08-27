package br.com.talesgardem.spacefox.pobredinheirinho.schemas;

public abstract class Schema {
    private String id;

    public Schema() {
    }

    public Schema(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
