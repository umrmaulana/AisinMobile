package com.example.aisin.model;

public class OrderDetailModel {
    private int id;
    private String no_po;
    private String no_part;
    private int qty;
    private String created_at;

    public OrderDetailModel(int id, String no_po, String no_part, int qty, String created_at) {
        this.id = id;
        this.no_po = no_po;
        this.no_part = no_part;
        this.qty = qty;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNo_po() {
        return no_po;
    }

    public void setNo_po(String no_po) {
        this.no_po = no_po;
    }

    public String getNo_part() {
        return no_part;
    }

    public void setNo_part(String no_part) {
        this.no_part = no_part;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
