package com.deepak.garageinventory.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "invoice_items",
    foreignKeys = @ForeignKey(
        entity = CustomerInvoice.class,
        parentColumns = "id",
        childColumns = "invoice_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("invoice_id")}
)
public class InvoiceItem {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "invoice_id")
    private long invoiceId;

    @ColumnInfo(name = "item_id")
    private long itemId;

    @ColumnInfo(name = "item_name")
    private String itemName;

    @ColumnInfo(name = "item_barcode")
    private String itemBarcode;

    @ColumnInfo(name = "unit_price")
    private double unitPrice;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "total_price")
    private double totalPrice;

    public InvoiceItem(long invoiceId, long itemId, String itemName, String itemBarcode,
                       double unitPrice, int quantity, double totalPrice) {
        this.invoiceId = invoiceId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemBarcode = itemBarcode;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemBarcode() {
        return itemBarcode;
    }

    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
