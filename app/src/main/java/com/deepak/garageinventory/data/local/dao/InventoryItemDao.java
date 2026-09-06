package com.deepak.garageinventory.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.deepak.garageinventory.data.local.entity.InventoryItem;

import java.util.List;

@Dao
public interface InventoryItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(InventoryItem item);

    @Update
    void update(InventoryItem item);

    @Delete
    void delete(InventoryItem item);

    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    LiveData<List<InventoryItem>> getAllItems();

    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    List<InventoryItem> getAllItemsSync();

    @Query("SELECT * FROM inventory_items WHERE id = :id LIMIT 1")
    LiveData<InventoryItem> getItemById(long id);

    @Query("SELECT * FROM inventory_items WHERE id = :id LIMIT 1")
    InventoryItem getItemByIdSync(long id);

    @Query("SELECT * FROM inventory_items WHERE barcode_sku = :barcodeSku LIMIT 1")
    InventoryItem getItemByBarcodeSync(String barcodeSku);

    @Query("SELECT * FROM inventory_items WHERE barcode_sku = :barcodeSku LIMIT 1")
    LiveData<InventoryItem> getItemByBarcode(String barcodeSku);

    @Query("SELECT * FROM inventory_items WHERE bin_id = :binId ORDER BY name ASC")
    LiveData<List<InventoryItem>> getItemsByBinId(long binId);

    @Query("SELECT * FROM inventory_items WHERE group_category = :group ORDER BY name ASC")
    LiveData<List<InventoryItem>> getItemsByGroup(String group);

    @Query("SELECT * FROM inventory_items WHERE quantity <= min_stock_alert ORDER BY quantity ASC")
    LiveData<List<InventoryItem>> getLowStockItems();

    @Query("SELECT * FROM inventory_items WHERE name LIKE '%' || :query || '%' OR barcode_sku LIKE '%' || :query || '%' OR group_category LIKE '%' || :query || '%' OR location_bin LIKE '%' || :query || '%' ORDER BY name ASC")
    LiveData<List<InventoryItem>> searchItems(String query);

    @Query("SELECT COUNT(*) FROM inventory_items")
    LiveData<Integer> getTotalItemCount();

    @Query("SELECT COUNT(*) FROM inventory_items WHERE quantity <= min_stock_alert")
    LiveData<Integer> getLowStockCount();

    @Query("SELECT SUM(quantity * unit_price) FROM inventory_items")
    LiveData<Double> getTotalInventoryValue();
}
