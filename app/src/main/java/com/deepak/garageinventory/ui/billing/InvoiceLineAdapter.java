package com.deepak.garageinventory.ui.billing;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepak.garageinventory.data.local.entity.InvoiceItem;
import com.deepak.garageinventory.databinding.ItemInvoiceLineBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InvoiceLineAdapter extends RecyclerView.Adapter<InvoiceLineAdapter.InvoiceLineViewHolder> {

    public interface OnLineItemRemoveListener {
        void onRemoveLineItem(int position);
    }

    private List<InvoiceItem> lineItems = new ArrayList<>();
    private final OnLineItemRemoveListener removeListener;

    public InvoiceLineAdapter(OnLineItemRemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    public void setLineItems(List<InvoiceItem> items) {
        this.lineItems = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InvoiceLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInvoiceLineBinding binding = ItemInvoiceLineBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new InvoiceLineViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceLineViewHolder holder, int position) {
        InvoiceItem item = lineItems.get(position);
        holder.bind(item, position, removeListener);
    }

    @Override
    public int getItemCount() {
        return lineItems.size();
    }

    static class InvoiceLineViewHolder extends RecyclerView.ViewHolder {
        private final ItemInvoiceLineBinding binding;

        public InvoiceLineViewHolder(ItemInvoiceLineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(InvoiceItem item, int position, OnLineItemRemoveListener listener) {
            binding.tvLineItemName.setText(item.getItemName());
            binding.tvLineUnitPrice.setText(String.format(Locale.US, "$%.2f x %d", item.getUnitPrice(), item.getQuantity()));
            binding.tvLineTotalPrice.setText(String.format(Locale.US, "$%.2f", item.getTotalPrice()));

            binding.btnRemoveLineItem.setOnClickListener(v -> {
                if (listener != null) listener.onRemoveLineItem(position);
            });
        }
    }
}
