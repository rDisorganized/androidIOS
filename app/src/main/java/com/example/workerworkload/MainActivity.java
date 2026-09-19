package com.example.workerworkload;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etWorkerName, etWorkType, etQuantity, etUnit, etHours;
    private MaterialButton btnCalculate;
    private MaterialCardView cardResult;
    private TextView tvResult;
    private WorkAdapter adapter;
    private final List<WorkItem> workList = new ArrayList<>();
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
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        etWorkerName = findViewById(R.id.etWorkerName);
        etWorkType = findViewById(R.id.etWorkType);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etHours = findViewById(R.id.etHours);
        btnCalculate = findViewById(R.id.btnCalculate);
        cardResult = findViewById(R.id.cardResult);
        tvResult = findViewById(R.id.tvResult);
        RecyclerView rvHistory = findViewById(R.id.rvHistory);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkAdapter(workList, this::confirmDelete);
        rvHistory.setAdapter(adapter);

        btnCalculate.setOnClickListener(v -> calculateAndSave());
        loadHistory();
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
            double quantity = Double.parseDouble(qtyStr);
            double hours = Double.parseDouble(hoursStr);

            if (quantity < 0) {
                Toast.makeText(this, "Khối lượng không được âm", Toast.LENGTH_SHORT).show();
                return;
            }
            if (hours <= 0) {
                Toast.makeText(this, "Thời gian phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            double productivity = quantity / hours;

            String resultText = String.format(Locale.getDefault(),
                    "Năng suất: %.2f %s / giờ\nTổng khối lượng: %.2f %s trong %.1f giờ",
                    productivity, unit, quantity, unit, hours);
            tvResult.setText(resultText);
            cardResult.setVisibility(View.VISIBLE);

            WorkItem item = new WorkItem(name, type, quantity, unit, hours, productivity);
            String key = FirebaseHelper.getUserWorksRef().push().getKey();
            if (key != null) {
                item.id = key;
                FirebaseHelper.getUserWorksRef().child(key).setValue(item)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
                            clearForm();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Lỗi lưu: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
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
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show());
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

    private void loadHistory() {
        FirebaseHelper.getUserWorksRef().orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        workList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            WorkItem item = child.getValue(WorkItem.class);
                            if (item != null) {
                                item.id = child.getKey();
                                workList.add(item);
                            }
                        }
                        Collections.reverse(workList);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this,
                                "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String textOf(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
