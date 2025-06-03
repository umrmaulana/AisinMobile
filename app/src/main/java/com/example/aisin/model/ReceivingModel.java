package com.example.aisin.model;

public class ReceivingModel {
    private int id;
    private String no_pkb;
    private String received_at;
    
    // Constructor
    public ReceivingModel(int id, String no_pkb, String received_at) {
        this.id = id;
        this.no_pkb = no_pkb;
        this.received_at = received_at;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNo_pkb() {
        return no_pkb;
    }
    
    public void setNo_pkb(String no_pkb) {
        this.no_pkb = no_pkb;
    }
    
    public String getReceived_at() {
        return received_at;
    }
    
    public void setReceived_at(String received_at) {
        this.received_at = received_at;
    }
}
