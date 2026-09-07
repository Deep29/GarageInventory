package com.deepak.garageinventory.ui.inventory;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deepak.garageinventory.data.local.entity.InventoryItem;
import com.deepak.garageinventory.databinding.ItemInventoryPartBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(InventoryItem item);
        void onDeleteClick(InventoryItem item);
    }

    private List<InventoryItem> itemList = new ArrayList<>();
    private final OnItemClickListener listener;

    public InventoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItemList(List<InventoryItem> items) {
        this.itemList = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInventoryPartBinding binding = ItemInventoryPartBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new InventoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = itemList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemInventoryPartBinding binding;

        public InventoryViewHolder(ItemInventoryPartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(InventoryItem item, OnItemClickListener listener) {
            binding.tvPartName.setText(item.getName());
            binding.tvPartSku.setText("SKU/Barcode: " + item.getBarcodeSku());

            String category = item.getGroupCategory() != null ? item.getGroupCategory() : "General";
            String location = item.getLocationBin() != null ? item.getLocationBin() : "Unassigned";
            binding.tvPartCategoryAndBin.setText(category + " • " + location);

            String priceText = String.format(Locale.US, "$%.2f / %s", item.getUnitPrice(), item.getUnitType());
            binding.tvUnitPrice.setText(priceText);

            binding.tvQuantityBadge.setText("Qty: " + item.getQuantity());

            if (item.getQuantity() <= item.getMinStockAlert()) {
                binding.tvLowStockWarning.setVisibility(View.VISIBLE);
                binding.tvQuantityBadge.setBackgroundColor(Color.parseColor("#D32F2F")); // Red
            } else {
                binding.tvLowStockWarning.setVisibility(View.GONE);
                binding.tvQuantityBadge.setBackgroundColor(Color.parseColor("#2E7D32")); // Green
            }

            if (item.getLocalImagePath() != null && !item.getLocalImagePath().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(new File(item.getLocalImagePath()))
                        .centerCrop()
                        .into(binding.ivPartImage);
            } else {
                binding.ivPartImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });

            binding.btnItemDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(item);
            });
        }
    }
}
