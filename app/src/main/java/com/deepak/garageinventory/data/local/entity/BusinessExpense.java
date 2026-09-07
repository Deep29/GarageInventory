package com.deepak.garageinventory.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "business_expenses")
public class BusinessExpense {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    public BusinessExpense(String category, double amount, String notes, long timestamp) {
        this.category = category;
        this.amount = amount;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
