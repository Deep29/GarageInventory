package com.deepak.garageinventory.ui.billing;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepak.garageinventory.data.local.entity.CustomerInvoice;
import com.deepak.garageinventory.databinding.ItemCustomerInvoiceBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceHistoryAdapter extends RecyclerView.Adapter<InvoiceHistoryAdapter.InvoiceHistoryViewHolder> {

    public interface OnInvoiceClickListener {
        void onInvoiceClick(CustomerInvoice invoice);
    }

    private List<CustomerInvoice> invoiceList = new ArrayList<>();
    private final OnInvoiceClickListener listener;

    public InvoiceHistoryAdapter(OnInvoiceClickListener listener) {
        this.listener = listener;
    }

    public void setInvoiceList(List<CustomerInvoice> invoices) {
        this.invoiceList = invoices != null ? invoices : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InvoiceHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerInvoiceBinding binding = ItemCustomerInvoiceBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new InvoiceHistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceHistoryViewHolder holder, int position) {
        CustomerInvoice invoice = invoiceList.get(position);
        holder.bind(invoice, listener);
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    static class InvoiceHistoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCustomerInvoiceBinding binding;

        public InvoiceHistoryViewHolder(ItemCustomerInvoiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CustomerInvoice invoice, OnInvoiceClickListener listener) {
            binding.tvInvoiceNo.setText(invoice.getInvoiceNumber());
            binding.tvCustomerInfo.setText("Customer: " + (invoice.getCustomerName() != null ? invoice.getCustomerName() : "Walk-in"));

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.US);
            binding.tvInvoiceDate.setText(sdf.format(new Date(invoice.getTimestamp())));

            binding.tvInvoiceTotal.setText(String.format(Locale.US, "$%.2f", invoice.getTotalAmount()));
            binding.tvPaymentMode.setText(invoice.getPaymentMode() != null ? invoice.getPaymentMode() : "Cash");

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onInvoiceClick(invoice);
            });
        }
    }
}
