package com.deepak.garageinventory.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.deepak.garageinventory.data.local.dao.CustomerInvoiceDao;
import com.deepak.garageinventory.data.local.dao.InvoiceItemDao;
import com.deepak.garageinventory.data.local.dao.InventoryItemDao;
import com.deepak.garageinventory.data.local.dao.StorageBinDao;
import com.deepak.garageinventory.data.local.dao.StockTransactionDao;
import com.deepak.garageinventory.data.local.entity.CustomerInvoice;
import com.deepak.garageinventory.data.local.entity.InvoiceItem;
import com.deepak.garageinventory.data.local.entity.InventoryItem;
import com.deepak.garageinventory.data.local.entity.StorageBin;
import com.deepak.garageinventory.data.local.entity.StockTransaction;

@Database(
    entities = {
        StorageBin.class,
        InventoryItem.class,
        StockTransaction.class,
        CustomerInvoice.class,
        InvoiceItem.class
    },
    version = 2,
    exportSchema = false
)
public abstract class InventoryDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "garage_inventory_db";
    private static volatile InventoryDatabase INSTANCE;

    public abstract StorageBinDao storageBinDao();
    public abstract InventoryItemDao inventoryItemDao();
    public abstract StockTransactionDao stockTransactionDao();
    public abstract CustomerInvoiceDao customerInvoiceDao();
    public abstract InvoiceItemDao invoiceItemDao();

    public static InventoryDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (InventoryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            InventoryDatabase.class,
                            DATABASE_NAME
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
