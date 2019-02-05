package com.example.tarun.grocerylist.grocerylist.Model;

public class GroceryItem {
    private String name;
    private String quantity;
    private String dateItemAdded;
    private int id;

    public GroceryItem(){

    }

    public GroceryItem(int id, String name, String quantity, String dateItemAdded) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.dateItemAdded = dateItemAdded;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDateItemAdded() {
        return dateItemAdded;
    }

    public void setDateItemAdded(String dateItemAdded) {
        this.dateItemAdded = dateItemAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
