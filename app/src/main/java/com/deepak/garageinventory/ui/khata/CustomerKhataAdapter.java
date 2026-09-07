package com.deepak.garageinventory.ui.khata;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepak.garageinventory.data.local.entity.CustomerKhata;
import com.deepak.garageinventory.databinding.ItemCustomerKhataBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerKhataAdapter extends RecyclerView.Adapter<CustomerKhataAdapter.CustomerKhataViewHolder> {

    private List<CustomerKhata> partyList = new ArrayList<>();

    public void setPartyList(List<CustomerKhata> list) {
        this.partyList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerKhataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerKhataBinding binding = ItemCustomerKhataBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CustomerKhataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerKhataViewHolder holder, int position) {
        CustomerKhata party = partyList.get(position);
        holder.bind(party);
    }

    @Override
    public int getItemCount() {
        return partyList.size();
    }

    static class CustomerKhataViewHolder extends RecyclerView.ViewHolder {
        private final ItemCustomerKhataBinding binding;

        public CustomerKhataViewHolder(ItemCustomerKhataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CustomerKhata party) {
            binding.tvPartyName.setText(party.getPartyName());
            binding.tvPartyPhone.setText("Ph: " + (party.getPartyPhone() != null ? party.getPartyPhone() : "N/A"));
            binding.tvDueBalance.setText(String.format(Locale.US, "Due: $%.2f", party.getDueBalance()));
        }
    }
}
