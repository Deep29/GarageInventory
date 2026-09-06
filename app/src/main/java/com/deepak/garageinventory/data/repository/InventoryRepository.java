package com.deepak.garageinventory.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.deepak.garageinventory.data.local.InventoryDatabase;
import com.deepak.garageinventory.data.local.dao.InventoryItemDao;
import com.deepak.garageinventory.data.local.dao.StorageBinDao;
import com.deepak.garageinventory.data.local.dao.StockTransactionDao;
import com.deepak.garageinventory.data.local.entity.InventoryItem;
import com.deepak.garageinventory.data.local.entity.StorageBin;
import com.deepak.garageinventory.data.local.entity.StockTransaction;
import com.deepak.garageinventory.utils.AppExecutors;

import java.util.List;

public class InventoryRepository {

    private final StorageBinDao storageBinDao;
    private final InventoryItemDao inventoryItemDao;
    private final StockTransactionDao stockTransactionDao;
    private final AppExecutors appExecutors;

    public interface OnItemInsertedListener {
        void onItemInserted(long itemId);
    }

    public interface OnBinInsertedListener {
        void onBinInserted(long binId);
    }

    public InventoryRepository(Application application) {
        InventoryDatabase db = InventoryDatabase.getInstance(application);
        this.storageBinDao = db.storageBinDao();
        this.inventoryItemDao = db.inventoryItemDao();
        this.stockTransactionDao = db.stockTransactionDao();
        this.appExecutors = AppExecutors.getInstance();
    }

    // --- Storage Bins ---
    public LiveData<List<StorageBin>> getAllBins() {
        return storageBinDao.getAllBins();
    }

    public LiveData<StorageBin> getBinById(long id) {
        return storageBinDao.getBinById(id);
    }

    public LiveData<StorageBin> getBinByCode(String code) {
        return storageBinDao.getBinByCode(code);
    }

    public void insertBin(StorageBin bin, OnBinInsertedListener listener) {
        appExecutors.diskIO().execute(() -> {
            long binId = storageBinDao.insert(bin);
            if (listener != null) {
                appExecutors.mainThread().execute(() -> listener.onBinInserted(binId));
            }
        });
    }

    public void updateBin(StorageBin bin) {
        appExecutors.diskIO().execute(() -> storageBinDao.update(bin));
    }

    public void deleteBin(StorageBin bin) {
        appExecutors.diskIO().execute(() -> storageBinDao.delete(bin));
    }

    // --- Inventory Items ---
    public LiveData<List<InventoryItem>> getAllItems() {
        return inventoryItemDao.getAllItems();
    }

    public LiveData<InventoryItem> getItemById(long id) {
        return inventoryItemDao.getItemById(id);
    }

    public LiveData<InventoryItem> getItemByBarcode(String barcode) {
        return inventoryItemDao.getItemByBarcode(barcode);
    }

    public LiveData<List<InventoryItem>> getItemsByBinId(long binId) {
        return inventoryItemDao.getItemsByBinId(binId);
    }

    public LiveData<List<InventoryItem>> getLowStockItems() {
        return inventoryItemDao.getLowStockItems();
    }

    public LiveData<List<InventoryItem>> searchItems(String query) {
        return inventoryItemDao.searchItems(query);
    }

    public LiveData<Integer> getTotalItemCount() {
        return inventoryItemDao.getTotalItemCount();
    }

    public LiveData<Integer> getLowStockCount() {
        return inventoryItemDao.getLowStockCount();
    }

    public LiveData<Double> getTotalInventoryValue() {
        return inventoryItemDao.getTotalInventoryValue();
    }

    public void insertItem(InventoryItem item, OnItemInsertedListener listener) {
        appExecutors.diskIO().execute(() -> {
            long itemId = inventoryItemDao.insert(item);
            if (listener != null) {
                appExecutors.mainThread().execute(() -> listener.onItemInserted(itemId));
            }
        });
    }

    public void updateItem(InventoryItem item) {
        appExecutors.diskIO().execute(() -> inventoryItemDao.update(item));
    }

    public void deleteItem(InventoryItem item) {
        appExecutors.diskIO().execute(() -> inventoryItemDao.delete(item));
    }

    // --- Stock Transactions ---
    public LiveData<List<StockTransaction>> getTransactionsForItem(long itemId) {
        return stockTransactionDao.getTransactionsForItem(itemId);
    }

    public void recordStockTransaction(long itemId, String type, int changeQty, String notes, String userEmail) {
        appExecutors.diskIO().execute(() -> {
            InventoryItem item = inventoryItemDao.getItemByIdSync(itemId);
            if (item != null) {
                int newQty = item.getQuantity();
                if ("STOCK_IN".equalsIgnoreCase(type)) {
                    newQty += changeQty;
                } else if ("STOCK_OUT".equalsIgnoreCase(type)) {
                    newQty -= changeQty;
                    if (newQty < 0) newQty = 0;
                } else if ("ADJUSTMENT".equalsIgnoreCase(type)) {
                    newQty = changeQty;
                }

                item.setQuantity(newQty);
                item.setUpdatedAt(System.currentTimeMillis());
                inventoryItemDao.update(item);

                StockTransaction tx = new StockTransaction(
                        itemId, type, changeQty, notes, userEmail, System.currentTimeMillis()
                );
                stockTransactionDao.insert(tx);
            }
        });
    }
}
