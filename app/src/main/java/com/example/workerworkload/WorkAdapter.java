package com.example.workerworkload;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkAdapter extends ListAdapter<WorkItem, WorkAdapter.ViewHolder> {

    public interface OnItemLongClickListener {
        void onLongClick(WorkItem item);
    }

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    private static final DiffUtil.ItemCallback<WorkItem> DIFF =
            new DiffUtil.ItemCallback<WorkItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull WorkItem a, @NonNull WorkItem b) {
                    if (a.id == null || b.id == null) {
                        return false;
                    }
                    return a.id.equals(b.id);
                }

                @Override
                public boolean areContentsTheSame(@NonNull WorkItem a, @NonNull WorkItem b) {
                    return a.sameContent(b);
                }
            };

    private final OnItemLongClickListener longClickListener;

    public WorkAdapter(OnItemLongClickListener longClickListener) {
        super(DIFF);
        this.longClickListener = longClickListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        String id = getItem(position).id;
        return id == null ? RecyclerView.NO_ID : id.hashCode();
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
        WorkItem item = getItem(position);
        holder.tvWorker.setText(safe(item.workerName));
        holder.tvWorkType.setText(safe(item.workType));
        holder.tvDetail.setText(String.format(Locale.getDefault(),
                "Khối lượng: %.2f %s  |  Thời gian: %.1f giờ",
                item.quantity, safe(item.unit), item.hours));
        holder.tvProductivity.setText(String.format(Locale.getDefault(),
                "Năng suất: %.2f %s/giờ", item.productivity, safe(item.unit)));
        if (item.timestamp > 0) {
            holder.tvDate.setText(DATE_FORMAT.format(new Date(item.timestamp)));
        } else {
            holder.tvDate.setText("");
        }
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(item);
            }
            return true;
        });
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvWorker, tvWorkType, tvDetail, tvProductivity, tvDate;

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
