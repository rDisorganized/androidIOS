package com.example.workerworkload;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etWorkerName, etWorkType, etQuantity, etUnit, etHours;
    private MaterialCardView cardResult;
    private TextView tvResult, tvEmpty;
    private ProgressBar progressBar;
    private WorkAdapter adapter;
    private final WorkRepository repository = new WorkRepository();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etWorkerName = findViewById(R.id.etWorkerName);
        etWorkType = findViewById(R.id.etWorkType);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etHours = findViewById(R.id.etHours);
        cardResult = findViewById(R.id.cardResult);
        tvResult = findViewById(R.id.tvResult);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);
        RecyclerView rvHistory = findViewById(R.id.rvHistory);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setHasFixedSize(true);
        rvHistory.setItemAnimator(null);
        adapter = new WorkAdapter(this::confirmDelete);
        rvHistory.setAdapter(adapter);

        findViewById(R.id.btnCalculate).setOnClickListener(v -> calculateAndSave());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        if (adapter.getItemCount() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
        repository.start(new WorkRepository.Callback() {
            @Override
            public void onLoaded(List<WorkItem> items) {
                progressBar.setVisibility(View.GONE);
                adapter.submitList(new ArrayList<>(items));
                tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        repository.stop();
        super.onStop();
    }

    private void calculateAndSave() {
        String name = textOf(etWorkerName);
        String type = textOf(etWorkType);
        String qtyStr = textOf(etQuantity);
        String unit = textOf(etUnit);
        String hoursStr = textOf(etHours);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(type)
                || TextUtils.isEmpty(qtyStr) || TextUtils.isEmpty(unit)
                || TextUtils.isEmpty(hoursStr)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double quantity = Double.parseDouble(qtyStr.replace(',', '.'));
            double hours = Double.parseDouble(hoursStr.replace(',', '.'));

            if (quantity < 0) {
                Toast.makeText(this, "Khối lượng không được âm", Toast.LENGTH_SHORT).show();
                return;
            }
            if (hours <= 0) {
                Toast.makeText(this, "Thời gian phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            double productivity = quantity / hours;
            tvResult.setText(String.format(Locale.getDefault(),
                    "Năng suất: %.2f %s / giờ\nTổng khối lượng: %.2f %s trong %.1f giờ",
                    productivity, unit, quantity, unit, hours));
            cardResult.setVisibility(View.VISIBLE);

            WorkItem item = new WorkItem(name, type, quantity, unit, hours, productivity);
            String key = FirebaseHelper.getUserWorksRef().push().getKey();
            if (key == null) {
                Toast.makeText(this, "Không tạo được mã bản ghi", Toast.LENGTH_SHORT).show();
                return;
            }
            item.id = key;
            FirebaseHelper.getUserWorksRef().child(key).setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        clearForm();
                        Toast.makeText(this, "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Lỗi lưu: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số liệu không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete(WorkItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bản ghi")
                .setMessage("Xóa công việc của " + item.workerName + "?")
                .setPositiveButton("Xóa", (d, w) -> {
                    if (item.id != null) {
                        FirebaseHelper.getUserWorksRef().child(item.id).removeValue()
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Không xóa được: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show());
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void clearForm() {
        etWorkerName.setText("");
        etWorkType.setText("");
        etQuantity.setText("");
        etUnit.setText("");
        etHours.setText("");
    }

    private String textOf(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            repository.stop();
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
