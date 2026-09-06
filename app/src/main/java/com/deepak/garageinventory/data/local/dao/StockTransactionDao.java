package com.deepak.garageinventory.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.deepak.garageinventory.data.local.entity.StockTransaction;

import java.util.List;

@Dao
public interface StockTransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(StockTransaction transaction);

    @Query("SELECT * FROM stock_transactions WHERE item_id = :itemId ORDER BY timestamp DESC")
    LiveData<List<StockTransaction>> getTransactionsForItem(long itemId);

    @Query("SELECT * FROM stock_transactions ORDER BY timestamp DESC")
    LiveData<List<StockTransaction>> getAllTransactions();

    @Query("SELECT * FROM stock_transactions ORDER BY timestamp DESC")
    List<StockTransaction> getAllTransactionsSync();
}
