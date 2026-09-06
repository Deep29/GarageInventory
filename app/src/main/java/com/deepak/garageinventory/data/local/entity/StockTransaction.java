package com.deepak.garageinventory.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "stock_transactions",
    foreignKeys = @ForeignKey(
        entity = InventoryItem.class,
        parentColumns = "id",
        childColumns = "item_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("item_id")}
)
public class StockTransaction {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "item_id")
    private long itemId;

    @ColumnInfo(name = "transaction_type")
    private String transactionType; // "STOCK_IN", "STOCK_OUT", "ADJUSTMENT"

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "user_email")
    private String userEmail;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    public StockTransaction(long itemId, String transactionType, int quantity,
                            String notes, String userEmail, long timestamp) {
        this.itemId = itemId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.notes = notes;
        this.userEmail = userEmail;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
