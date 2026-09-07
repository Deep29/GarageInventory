package com.deepak.garageinventory.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.deepak.garageinventory.data.local.entity.CustomerInvoice;

import java.util.List;

@Dao
public interface CustomerInvoiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertInvoice(CustomerInvoice invoice);

    @Query("SELECT * FROM customer_invoices ORDER BY timestamp DESC")
    LiveData<List<CustomerInvoice>> getAllInvoices();

    @Query("SELECT * FROM customer_invoices WHERE id = :id LIMIT 1")
    LiveData<CustomerInvoice> getInvoiceById(long id);

    @Query("SELECT * FROM customer_invoices WHERE id = :id LIMIT 1")
    CustomerInvoice getInvoiceByIdSync(long id);

    @Query("SELECT COUNT(*) FROM customer_invoices")
    LiveData<Integer> getInvoiceCount();

    @Query("SELECT SUM(total_amount) FROM customer_invoices")
    LiveData<Double> getTotalSalesAmount();
}
