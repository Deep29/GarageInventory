package com.deepak.garageinventory.ui.expenses;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepak.garageinventory.data.local.entity.BusinessExpense;
import com.deepak.garageinventory.databinding.ItemBusinessExpenseBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<BusinessExpense> expenseList = new ArrayList<>();

    public void setExpenseList(List<BusinessExpense> list) {
        this.expenseList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBusinessExpenseBinding binding = ItemBusinessExpenseBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ExpenseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        BusinessExpense expense = expenseList.get(position);
        holder.bind(expense);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final ItemBusinessExpenseBinding binding;

        public ExpenseViewHolder(ItemBusinessExpenseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BusinessExpense expense) {
            binding.tvExpenseCategory.setText(expense.getCategory());
            binding.tvExpenseNotes.setText(expense.getNotes() != null ? expense.getNotes() : "");
            binding.tvExpenseAmount.setText(String.format(Locale.US, "-$%.2f", expense.getAmount()));
        }
    }
}
