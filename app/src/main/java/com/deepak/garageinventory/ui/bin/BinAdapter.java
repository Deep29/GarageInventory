package com.deepak.garageinventory.ui.bin;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepak.garageinventory.data.local.entity.StorageBin;
import com.deepak.garageinventory.databinding.ItemStorageBinBinding;
import com.deepak.garageinventory.utils.QrCodeGenerator;

import java.util.ArrayList;
import java.util.List;

public class BinAdapter extends RecyclerView.Adapter<BinAdapter.BinViewHolder> {

    public interface OnBinClickListener {
        void onBinClick(StorageBin bin);
        void onPrintClick(StorageBin bin);
    }

    private List<StorageBin> binList = new ArrayList<>();
    private final OnBinClickListener listener;

    public BinAdapter(OnBinClickListener listener) {
        this.listener = listener;
    }

    public void setBinList(List<StorageBin> bins) {
        this.binList = bins != null ? bins : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStorageBinBinding binding = ItemStorageBinBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new BinViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BinViewHolder holder, int position) {
        StorageBin bin = binList.get(position);
        holder.bind(bin, listener);
    }

    @Override
    public int getItemCount() {
        return binList.size();
    }

    static class BinViewHolder extends RecyclerView.ViewHolder {
        private final ItemStorageBinBinding binding;

        public BinViewHolder(ItemStorageBinBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(StorageBin bin, OnBinClickListener listener) {
            binding.tvBinName.setText(bin.getBinName());
            binding.tvBinCode.setText("Bin Code: " + bin.getBinCode());
            binding.tvBinDesc.setText(bin.getDescription() != null ? bin.getDescription() : "");

            try {
                Bitmap qrBitmap = QrCodeGenerator.generateQrCode(bin.getBinCode(), 150, 150);
                if (qrBitmap != null) {
                    binding.ivBinQr.setImageBitmap(qrBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onBinClick(bin);
            });

            binding.btnPrintLabel.setOnClickListener(v -> {
                if (listener != null) listener.onPrintClick(bin);
            });
        }
    }
}
