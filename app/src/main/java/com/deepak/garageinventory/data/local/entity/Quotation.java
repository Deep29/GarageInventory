package com.deepak.garageinventory.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quotations")
public class Quotation {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "quotation_number")
    private String quotationNumber;

    @ColumnInfo(name = "customer_name")
    private String customerName;

    @ColumnInfo(name = "customer_phone")
    private String customerPhone;

    @ColumnInfo(name = "total_amount")
    private double totalAmount;

    @ColumnInfo(name = "status")
    private String status; // PENDING, CONVERTED_TO_SALE

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    public Quotation(String quotationNumber, String customerName, String customerPhone,
                     double totalAmount, String status, long timestamp) {
        this.quotationNumber = quotationNumber;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.totalAmount = totalAmount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuotationNumber() {
        return quotationNumber;
    }

    public void setQuotationNumber(String quotationNumber) {
        this.quotationNumber = quotationNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
