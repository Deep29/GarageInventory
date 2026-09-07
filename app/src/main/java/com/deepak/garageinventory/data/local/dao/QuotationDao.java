package com.deepak.garageinventory.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.deepak.garageinventory.data.local.entity.Quotation;

import java.util.List;

@Dao
public interface QuotationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertQuotation(Quotation quotation);

    @Query("SELECT * FROM quotations ORDER BY timestamp DESC")
    LiveData<List<Quotation>> getAllQuotations();
}
