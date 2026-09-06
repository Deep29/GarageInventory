package com.deepak.garageinventory.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "inventory_items",
    indices = {@Index(value = {"barcode_sku"})}
)
public class InventoryItem {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "barcode_sku")
    private String barcodeSku;

    @ColumnInfo(name = "group_category")
    private String groupCategory;

    @ColumnInfo(name = "bin_id")
    private long binId;

    @ColumnInfo(name = "location_bin")
    private String locationBin;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "min_stock_alert")
    private int minStockAlert;

    @ColumnInfo(name = "unit_price")
    private double unitPrice;

    @ColumnInfo(name = "unit_type")
    private String unitType;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "drive_image_id")
    private String driveImageId;

    @ColumnInfo(name = "local_image_path")
    private String localImagePath;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public InventoryItem(String name, String barcodeSku, String groupCategory, long binId,
                         String locationBin, int quantity, int minStockAlert, double unitPrice,
                         String unitType, String description, String driveImageId,
                         String localImagePath, long createdAt, long updatedAt) {
        this.name = name;
        this.barcodeSku = barcodeSku;
        this.groupCategory = groupCategory;
        this.binId = binId;
        this.locationBin = locationBin;
        this.quantity = quantity;
        this.minStockAlert = minStockAlert;
        this.unitPrice = unitPrice;
        this.unitType = unitType;
        this.description = description;
        this.driveImageId = driveImageId;
        this.localImagePath = localImagePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcodeSku() {
        return barcodeSku;
    }

    public void setBarcodeSku(String barcodeSku) {
        this.barcodeSku = barcodeSku;
    }

    public String getGroupCategory() {
        return groupCategory;
    }

    public void setGroupCategory(String groupCategory) {
        this.groupCategory = groupCategory;
    }

    public long getBinId() {
        return binId;
    }

    public void setBinId(long binId) {
        this.binId = binId;
    }

    public String getLocationBin() {
        return locationBin;
    }

    public void setLocationBin(String locationBin) {
        this.locationBin = locationBin;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMinStockAlert() {
        return minStockAlert;
    }

    public void setMinStockAlert(int minStockAlert) {
        this.minStockAlert = minStockAlert;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDriveImageId() {
        return driveImageId;
    }

    public void setDriveImageId(String driveImageId) {
        this.driveImageId = driveImageId;
    }

    public String getLocalImagePath() {
        return localImagePath;
    }

    public void setLocalImagePath(String localImagePath) {
        this.localImagePath = localImagePath;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
