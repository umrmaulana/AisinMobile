package com.example.aisin.model;

public class OrderModel {
    private int id;
    private String no_po;
    private String created_at;
    
    // Constructor
    public OrderModel(int id, String no_po, String created_at) {
        this.id = id;
        this.no_po = no_po;
        this.created_at = created_at;
    }
    
    // Getters and Setters
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
    
    public String getCreated_at() {
        return created_at;
    }
    
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
