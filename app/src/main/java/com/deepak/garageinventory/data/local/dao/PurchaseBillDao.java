package com.deepak.garageinventory.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.deepak.garageinventory.data.local.entity.PurchaseBill;

import java.util.List;

@Dao
public interface PurchaseBillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPurchaseBill(PurchaseBill bill);

    @Query("SELECT * FROM purchase_bills ORDER BY timestamp DESC")
    LiveData<List<PurchaseBill>> getAllPurchaseBills();

    @Query("SELECT SUM(total_amount) FROM purchase_bills")
    LiveData<Double> getTotalPurchasesAmount();
}
