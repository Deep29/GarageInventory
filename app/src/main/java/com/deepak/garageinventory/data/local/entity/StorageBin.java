package com.deepak.garageinventory.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "storage_bins",
    indices = {@Index(value = {"bin_code"}, unique = true)}
)
public class StorageBin {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "bin_code")
    private String binCode;

    @ColumnInfo(name = "bin_name")
    private String binName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "qr_code_content")
    private String qrCodeContent;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public StorageBin(String binCode, String binName, String description, String qrCodeContent, long createdAt) {
        this.binCode = binCode;
        this.binName = binName;
        this.description = description;
        this.qrCodeContent = qrCodeContent;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public String getBinName() {
        return binName;
    }

    public void setBinName(String binName) {
        this.binName = binName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQrCodeContent() {
        return qrCodeContent;
    }

    public void setQrCodeContent(String qrCodeContent) {
        this.qrCodeContent = qrCodeContent;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
