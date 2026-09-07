package com.deepak.garageinventory.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.deepak.garageinventory.data.local.InventoryDatabase;
import com.deepak.garageinventory.data.local.dao.BusinessExpenseDao;
import com.deepak.garageinventory.data.local.dao.CustomerInvoiceDao;
import com.deepak.garageinventory.data.local.dao.CustomerKhataDao;
import com.deepak.garageinventory.data.local.dao.InvoiceItemDao;
import com.deepak.garageinventory.data.local.dao.InventoryItemDao;
import com.deepak.garageinventory.data.local.dao.PurchaseBillDao;
import com.deepak.garageinventory.data.local.dao.QuotationDao;
import com.deepak.garageinventory.data.local.dao.StorageBinDao;
import com.deepak.garageinventory.data.local.dao.StockTransactionDao;
import com.deepak.garageinventory.data.local.entity.BusinessExpense;
import com.deepak.garageinventory.data.local.entity.CustomerInvoice;
import com.deepak.garageinventory.data.local.entity.CustomerKhata;
import com.deepak.garageinventory.data.local.entity.InvoiceItem;
import com.deepak.garageinventory.data.local.entity.InventoryItem;
import com.deepak.garageinventory.data.local.entity.PurchaseBill;
import com.deepak.garageinventory.data.local.entity.Quotation;
import com.deepak.garageinventory.data.local.entity.StorageBin;
import com.deepak.garageinventory.data.local.entity.StockTransaction;
import com.deepak.garageinventory.utils.AppExecutors;

import java.util.List;

public class InventoryRepository {

    private final StorageBinDao storageBinDao;
    private final InventoryItemDao inventoryItemDao;
    private final StockTransactionDao stockTransactionDao;
    private final CustomerInvoiceDao customerInvoiceDao;
    private final InvoiceItemDao invoiceItemDao;
    private final BusinessExpenseDao businessExpenseDao;
    private final CustomerKhataDao customerKhataDao;
    private final QuotationDao quotationDao;
    private final PurchaseBillDao purchaseBillDao;
    private final AppExecutors appExecutors;

    public interface OnItemInsertedListener {
        void onItemInserted(long itemId);
    }

    public interface OnBinInsertedListener {
        void onBinInserted(long binId);
    }

    public interface OnInvoiceSavedListener {
        void onInvoiceSaved(long invoiceId);
    }

    public InventoryRepository(Application application) {
        InventoryDatabase db = InventoryDatabase.getInstance(application);
        this.storageBinDao = db.storageBinDao();
        this.inventoryItemDao = db.inventoryItemDao();
        this.stockTransactionDao = db.stockTransactionDao();
        this.customerInvoiceDao = db.customerInvoiceDao();
        this.invoiceItemDao = db.invoiceItemDao();
        this.businessExpenseDao = db.businessExpenseDao();
        this.customerKhataDao = db.customerKhataDao();
        this.quotationDao = db.quotationDao();
        this.purchaseBillDao = db.purchaseBillDao();
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

    // --- Billing & Invoices ---
    public LiveData<List<CustomerInvoice>> getAllInvoices() {
        return customerInvoiceDao.getAllInvoices();
    }

    public LiveData<CustomerInvoice> getInvoiceById(long id) {
        return customerInvoiceDao.getInvoiceById(id);
    }

    public LiveData<List<InvoiceItem>> getItemsForInvoice(long invoiceId) {
        return invoiceItemDao.getItemsForInvoice(invoiceId);
    }

    public LiveData<Integer> getInvoiceCount() {
        return customerInvoiceDao.getInvoiceCount();
    }

    public LiveData<Double> getTotalSalesAmount() {
        return customerInvoiceDao.getTotalSalesAmount();
    }

    public void saveInvoiceAndDeductStock(CustomerInvoice invoice, List<InvoiceItem> lineItems, OnInvoiceSavedListener listener) {
        appExecutors.diskIO().execute(() -> {
            long invoiceId = customerInvoiceDao.insertInvoice(invoice);
            if (lineItems != null && !lineItems.isEmpty()) {
                for (InvoiceItem item : lineItems) {
                    item.setInvoiceId(invoiceId);

                    // Auto-deduct quantity from stock
                    if (item.getItemId() > 0) {
                        InventoryItem stockItem = inventoryItemDao.getItemByIdSync(item.getItemId());
                        if (stockItem != null) {
                            int newQty = stockItem.getQuantity() - item.getQuantity();
                            if (newQty < 0) newQty = 0;
                            stockItem.setQuantity(newQty);
                            stockItem.setUpdatedAt(System.currentTimeMillis());
                            inventoryItemDao.update(stockItem);

                            // Record transaction log
                            StockTransaction tx = new StockTransaction(
                                    item.getItemId(),
                                    "STOCK_OUT",
                                    item.getQuantity(),
                                    "Billing Invoice: " + invoice.getInvoiceNumber(),
                                    "billing",
                                    System.currentTimeMillis()
                            );
                            stockTransactionDao.insert(tx);
                        }
                    }
                }
                invoiceItemDao.insertInvoiceItems(lineItems);
            }

            if (listener != null) {
                appExecutors.mainThread().execute(() -> listener.onInvoiceSaved(invoiceId));
            }
        });
    }

    // --- Expenses & Khata ---
    public LiveData<List<BusinessExpense>> getAllExpenses() {
        return businessExpenseDao.getAllExpenses();
    }

    public LiveData<Double> getTotalExpensesAmount() {
        return businessExpenseDao.getTotalExpensesAmount();
    }

    public void insertExpense(BusinessExpense expense) {
        appExecutors.diskIO().execute(() -> businessExpenseDao.insertExpense(expense));
    }

    public LiveData<List<CustomerKhata>> getAllCustomers() {
        return customerKhataDao.getAllCustomers();
    }

    public LiveData<Double> getTotalCustomerDues() {
        return customerKhataDao.getTotalCustomerDues();
    }

    public void insertParty(CustomerKhata party) {
        appExecutors.diskIO().execute(() -> customerKhataDao.insertParty(party));
    }

    // --- Quotations & Purchase Bills ---
    public LiveData<List<Quotation>> getAllQuotations() {
        return quotationDao.getAllQuotations();
    }

    public void insertQuotation(Quotation quotation) {
        appExecutors.diskIO().execute(() -> quotationDao.insertQuotation(quotation));
    }

    public LiveData<List<PurchaseBill>> getAllPurchaseBills() {
        return purchaseBillDao.getAllPurchaseBills();
    }

    public LiveData<Double> getTotalPurchasesAmount() {
        return purchaseBillDao.getTotalPurchasesAmount();
    }

    public void insertPurchaseBill(PurchaseBill bill) {
        appExecutors.diskIO().execute(() -> purchaseBillDao.insertPurchaseBill(bill));
    }
}
