package com.deepak.garageinventory.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.deepak.garageinventory.data.local.entity.BusinessExpense;

import java.util.List;

@Dao
public interface BusinessExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertExpense(BusinessExpense expense);

    @Query("SELECT * FROM business_expenses ORDER BY timestamp DESC")
    LiveData<List<BusinessExpense>> getAllExpenses();

    @Query("SELECT SUM(amount) FROM business_expenses")
    LiveData<Double> getTotalExpensesAmount();
}
