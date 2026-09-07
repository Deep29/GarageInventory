package com.deepak.garageinventory.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.deepak.garageinventory.data.local.entity.InvoiceItem;

import java.util.List;

@Dao
public interface InvoiceItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInvoiceItems(List<InvoiceItem> items);

    @Query("SELECT * FROM invoice_items WHERE invoice_id = :invoiceId")
    LiveData<List<InvoiceItem>> getItemsForInvoice(long invoiceId);

    @Query("SELECT * FROM invoice_items WHERE invoice_id = :invoiceId")
    List<InvoiceItem> getItemsForInvoiceSync(long invoiceId);
}
