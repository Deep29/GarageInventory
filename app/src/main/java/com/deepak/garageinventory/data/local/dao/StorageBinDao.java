package com.deepak.garageinventory.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.deepak.garageinventory.data.local.entity.StorageBin;

import java.util.List;

@Dao
public interface StorageBinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(StorageBin storageBin);

    @Update
    void update(StorageBin storageBin);

    @Delete
    void delete(StorageBin storageBin);

    @Query("SELECT * FROM storage_bins ORDER BY bin_name ASC")
    LiveData<List<StorageBin>> getAllBins();

    @Query("SELECT * FROM storage_bins WHERE bin_code = :binCode LIMIT 1")
    StorageBin getBinByCodeSync(String binCode);

    @Query("SELECT * FROM storage_bins WHERE bin_code = :binCode LIMIT 1")
    LiveData<StorageBin> getBinByCode(String binCode);

    @Query("SELECT * FROM storage_bins WHERE id = :id LIMIT 1")
    LiveData<StorageBin> getBinById(long id);

    @Query("SELECT COUNT(*) FROM storage_bins")
    LiveData<Integer> getBinCount();

    @Query("SELECT MAX(CAST(bin_code AS INTEGER)) FROM storage_bins WHERE bin_code GLOB '[0-9][0-9][0-9][0-9][0-9]'")
    Integer getMaxBinNumberSync();
}
