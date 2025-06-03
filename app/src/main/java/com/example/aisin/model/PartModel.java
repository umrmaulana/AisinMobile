package com.example.aisin.model;

public class PartModel {
    private String no_part;
    private int stok;
    private String status;

    // Constructor
    public PartModel(String no_part, int stok, String status) {
        this.no_part = no_part;
        this.stok = stok;
        this.status = status;
    }

    // Getters and Setters
    public String getPart_number() {
        return no_part;
    }

    public void setPart_number(String no_part) {
        this.no_part = no_part;
    }

    public int getStock() {
        return stok;
    }

    public void setStock(int stok) {
        this.stok = stok;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
