package com.example.aisin.model;

public class ProductionModel {
    private String id;
    private String no_fg;
    private int qty;
    private String created_at;
    
    // Constructor
    public ProductionModel(String id, String no_fg, int qty, String created_at) {
        this.id = id;
        this.no_fg = no_fg;
        this.qty = qty;
        this.created_at = created_at;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNo_fg() {
        return no_fg;
    }
    
    public void setNo_fg(String no_fg) {
        this.no_fg = no_fg;
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
