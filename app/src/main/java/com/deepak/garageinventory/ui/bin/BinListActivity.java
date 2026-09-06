package com.deepak.garageinventory.ui.bin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deepak.garageinventory.data.local.entity.StorageBin;
import com.deepak.garageinventory.data.repository.InventoryRepository;
import com.deepak.garageinventory.databinding.ActivityBinListBinding;
import com.deepak.garageinventory.utils.LabelPrintHelper;
import com.deepak.garageinventory.utils.QrCodeGenerator;

public class BinListActivity extends AppCompatActivity implements BinAdapter.OnBinClickListener {

    private ActivityBinListBinding binding;
    private InventoryRepository repository;
    private BinAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBinListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new InventoryRepository(getApplication());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new BinAdapter(this);
        binding.rvBins.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBins.setAdapter(adapter);

        binding.fabAddBin.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditBinActivity.class);
            startActivity(intent);
        });

        repository.getAllBins().observe(this, bins -> {
            if (bins == null || bins.isEmpty()) {
                binding.tvEmptyBins.setVisibility(View.VISIBLE);
                binding.rvBins.setVisibility(View.GONE);
            } else {
                binding.tvEmptyBins.setVisibility(View.GONE);
                binding.rvBins.setVisibility(View.VISIBLE);
                adapter.setBinList(bins);
            }
        });
    }

    @Override
    public void onBinClick(StorageBin bin) {
        Intent intent = new Intent(this, AddEditBinActivity.class);
        intent.putExtra("extra_bin_id", bin.getId());
        startActivity(intent);
    }

    @Override
    public void onPrintClick(StorageBin bin) {
        try {
            Bitmap qrBitmap = QrCodeGenerator.generateQrCode(bin.getBinCode(), 200, 200);
            Bitmap labelBitmap = LabelPrintHelper.createBinLabelBitmap(
                    bin.getBinName(),
                    bin.getBinCode(),
                    qrBitmap
            );
            LabelPrintHelper.printLabel(this, "BinLabel_" + bin.getBinCode(), labelBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
