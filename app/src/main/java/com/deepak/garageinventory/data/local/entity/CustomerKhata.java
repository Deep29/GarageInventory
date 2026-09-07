package com.deepak.garageinventory.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customer_khata")
public class CustomerKhata {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "party_name")
    private String partyName;

    @ColumnInfo(name = "party_phone")
    private String partyPhone;

    @ColumnInfo(name = "party_type")
    private String partyType; // "CUSTOMER" or "SUPPLIER"

    @ColumnInfo(name = "due_balance")
    private double dueBalance; // Positive = Customer owes us, Negative = We owe supplier

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    public CustomerKhata(String partyName, String partyPhone, String partyType, double dueBalance, long timestamp) {
        this.partyName = partyName;
        this.partyPhone = partyPhone;
        this.partyType = partyType;
        this.dueBalance = dueBalance;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getPartyPhone() {
        return partyPhone;
    }

    public void setPartyPhone(String partyPhone) {
        this.partyPhone = partyPhone;
    }

    public String getPartyType() {
        return partyType;
    }

    public void setPartyType(String partyType) {
        this.partyType = partyType;
    }

    public double getDueBalance() {
        return dueBalance;
    }

    public void setDueBalance(double dueBalance) {
        this.dueBalance = dueBalance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
