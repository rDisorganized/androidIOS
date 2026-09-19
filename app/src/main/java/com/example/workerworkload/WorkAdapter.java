package com.example.workerworkload;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {

    public interface OnItemLongClickListener {
        void onLongClick(WorkItem item);
    }

    private final List<WorkItem> list;
    private final OnItemLongClickListener longClickListener;

    public WorkAdapter(List<WorkItem> list, OnItemLongClickListener longClickListener) {
        this.list = list;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkItem item = list.get(position);
        holder.tvWorker.setText(item.workerName);
        holder.tvWorkType.setText(item.workType);
        holder.tvDetail.setText(String.format(Locale.getDefault(),
                "Khối lượng: %.2f %s  |  Thời gian: %.1f giờ",
                item.quantity, item.unit, item.hours));
        holder.tvProductivity.setText(String.format(Locale.getDefault(),
                "Năng suất: %.2f %s/giờ", item.productivity, item.unit));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(item.timestamp)));

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(item);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorker, tvWorkType, tvDetail, tvProductivity, tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvWorker = itemView.findViewById(R.id.tvWorker);
            tvWorkType = itemView.findViewById(R.id.tvWorkType);
            tvDetail = itemView.findViewById(R.id.tvDetail);
            tvProductivity = itemView.findViewById(R.id.tvProductivity);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
