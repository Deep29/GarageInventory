package com.deepak.garageinventory.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.deepak.garageinventory.data.local.entity.CustomerKhata;

import java.util.List;

@Dao
public interface CustomerKhataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertParty(CustomerKhata party);

    @Update
    void updateParty(CustomerKhata party);

    @Query("SELECT * FROM customer_khata ORDER BY party_name ASC")
    LiveData<List<CustomerKhata>> getAllParties();

    @Query("SELECT * FROM customer_khata WHERE party_type = 'CUSTOMER' ORDER BY party_name ASC")
    LiveData<List<CustomerKhata>> getAllCustomers();

    @Query("SELECT * FROM customer_khata WHERE party_type = 'SUPPLIER' ORDER BY party_name ASC")
    LiveData<List<CustomerKhata>> getAllSuppliers();

    @Query("SELECT SUM(due_balance) FROM customer_khata WHERE due_balance > 0")
    LiveData<Double> getTotalCustomerDues();
}
