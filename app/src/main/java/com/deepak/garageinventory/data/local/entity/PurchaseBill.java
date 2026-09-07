package com.deepak.garageinventory.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "purchase_bills")
public class PurchaseBill {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "bill_number")
    private String billNumber;

    @ColumnInfo(name = "supplier_name")
    private String supplierName;

    @ColumnInfo(name = "supplier_phone")
    private String supplierPhone;

    @ColumnInfo(name = "total_amount")
    private double totalAmount;

    @ColumnInfo(name = "payment_status")
    private String paymentStatus; // PAID, CREDIT_DUE

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    public PurchaseBill(String billNumber, String supplierName, String supplierPhone,
                        double totalAmount, String paymentStatus, long timestamp) {
        this.billNumber = billNumber;
        this.supplierName = supplierName;
        this.supplierPhone = supplierPhone;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
